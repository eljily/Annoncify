package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.ImageDto;
import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.dto.ProductRequestDto;
import com.sibrahim.annoncify.entity.*;
import com.sibrahim.annoncify.entity.enums.ProductStatus;
import com.sibrahim.annoncify.exceptions.GenericException;
import com.sibrahim.annoncify.exceptions.InvalidKeywordException;
import com.sibrahim.annoncify.exceptions.NotFoundException;
import com.sibrahim.annoncify.mapper.CategoryMapper;
import com.sibrahim.annoncify.mapper.ImageMapper;
import com.sibrahim.annoncify.mapper.ProductMapper;
import com.sibrahim.annoncify.repository.*;
import com.sibrahim.annoncify.services.CategoryService;
import com.sibrahim.annoncify.services.ProductService;
import com.sibrahim.annoncify.services.SubCategoryService;
import com.sibrahim.annoncify.services.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ImageRespository imageRespository;
    private final ImageServiceImpl imageService;
    private final ImageMapper imageMapper;
    @Lazy
    @Autowired
    private UserService userService;
    @Autowired
    private SubCategoryRepository subCategoryRepository;
    @Autowired
    private SubRegionRepository subRegionRepository;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, ImageRespository imageRespository, ImageServiceImpl imageService, CategoryService categoryService, ImageMapper imageMapper, CategoryMapper categoryMapper, CategoryRepository categoryRepository, SubCategoryService subCategoryService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.imageRespository = imageRespository;
        this.imageService = imageService;
        this.imageMapper = imageMapper;
    }


    @Override
    public Page<ProductDto> getAllProducts(int page,int size) {
            return productRepository
                    .findAllOrderedByCreateDateDesc(PageRequest.of(page,size))
                    .map(productMapper::toProductDto);
    }

    @Override
    public Page<ProductDto> getAllProducts(int page, int size, int subCategoryId) {
        try {
            return productRepository.findBySubCategory_Id(subCategoryId, PageRequest.of(page, size))
                    .map(productMapper::toProductDto);
        } catch (Exception e) {
            log.error("Error occurred while trying to fetch page of products by category", e);
            return null;
        }
    }

    @Override
    public Page<ProductDto> getAllProductsByCategoryId(int page, int size, int categoryId) {
        try {
            return productRepository.findAllByCategoryId(categoryId, PageRequest.of(page, size))
                    .map(productMapper::toProductDto);
        } catch (Exception e) {
            log.error("Error occurred while trying to fetch page of products by category", e);
            return null;
        }
    }

    @Override
    @Transactional
    public Page<ProductDto> getProductsByKeyword(int page, int size, String keyword) {
        return productRepository.getProductsByKeyword(keyword, PageRequest.of(page, size))
                .map(productMapper::toProductDto);
    }

    @Override
    public List<ProductDto> getProductsByUserId(Long id) {
        User user = userService.getById(id).orElseThrow(()->new NotFoundException("User not found"));
        List<Product> products = productRepository.findProductsByUserId(user.getId());
        return productMapper.toProductDtos(products);
    }

    @Transactional
    @Override
    public Optional<ProductDto> getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        log.info("Called get Product ");
        Long hit = product.getHit();
        Long hitValue = Optional.ofNullable(hit).orElse(0L);
        product.setHit(hitValue+1L);
        productRepository.save(product);
        return Optional.of(productMapper.toProductDto(product));
    }

    @Override
    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow();

        // Step 1: Get all images associated with the product
        List<Image> images = product.getImages();

        // Step 2: Delete each image asynchronously
        CompletableFuture<?>[] deletionFutures = images.stream()
                .map(image -> CompletableFuture.runAsync(() -> {
                    try {
                        imageService.deleteFileByUrl(image.getImageUrl());
                    } catch (Exception e) {
                        // Log the error or handle it as per your requirement
                        log.error("Error while deleting image from firebase ",e);
                    }
                }))
                .toArray(CompletableFuture[]::new);

        // Step 3: Wait for all image deletions to complete, then delete the product
        CompletableFuture.allOf(deletionFutures)
                .thenRun(() -> productRepository.deleteById(product.getId()))
                .join();
    }

    @Transactional
    @Override
    public ProductDto addProduct(ProductRequestDto productRequestDto) {
        Product product = new Product();

        product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        product.setDescription(productRequestDto.getDescription());
        SubRegion subRegion = subRegionRepository.findById(productRequestDto.getSubRegionId())
                .orElseThrow(()->new NotFoundException("Not Found Exception"));
        product.setSubRegion(subRegion);

        SubCategory subCategory ;
        if(productRequestDto.getSubCategoryId()==null){
            // Call the method to generate category and subcategory from AI asynchronously
            CompletableFuture<Map<String, String>> categoryAndSubcategoryFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    return imageService.generateCategoryAndSubcategory(productRequestDto.getImages());
                } catch (IOException e) {
                    log.error("Error generating category and subcategory: {}", e.getMessage());
                    return Collections.emptyMap();
                }
            });

            // Wait for the response from the AI
            Map<String, String> categoryAndSubcategory = categoryAndSubcategoryFuture.join();

            // Fetch or create category and subcategory based on the obtained data

            subCategory = subCategoryRepository
                    .findSubCategoryByName(categoryAndSubcategory
                            .get("subcategory"))
                    .orElseThrow(() -> new NotFoundException("Subcategory not found"));
        }
        else {
            subCategory = subCategoryRepository.findById(productRequestDto.getSubCategoryId()).get();
        }
            product.setSubCategory(subCategory);

            // Set the user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object principal = authentication.getPrincipal();

            if (principal instanceof User user) {
                product.setUser(user);
                user.setProductsCount(user.getProductsCount() + 1);
            } else {
                product.setUser(null);
            }
            product.setProductStatus(ProductStatus.PENDING);
            product.setCreateDate(new Date());
            product.setUpdateDate(new Date());
            log.info("-----Saving PRODUCT-------");
            Product savedProduct = productRepository.save(product);
            log.info("------Product Saved-------");

            // Upload images and save them to the database
            List<String> imageUrls = productRequestDto.getImages().parallelStream() // Used parallelStream() for concurrent processing
                    .map(imageService::uploadImageToFirebase)
                    .toList();

            List<ImageDto> imageDtos = new ArrayList<>();

            for (String imageUrl : imageUrls) {
                Image image = new Image();
                image.setImageUrl(imageUrl);
                image.setProduct(savedProduct);
                image.setCreateDate(LocalDateTime.now());
                image.setUpdateDate(LocalDateTime.now());
                imageDtos.add(imageMapper.toImageDto(image));
                imageRespository.save(image);
            }
            savedProduct.setImages(imageMapper.toImages(imageDtos));
            return productMapper.toProductDto(savedProduct);
    }

    @Override
    public List<Product> findLastEightProductsByCategoryId(Long categoryId) {
        Pageable pageable = PageRequest.of(0, 10); // Limit to 10 results
        return productRepository.findLastEightProductsByCategoryId(categoryId, pageable);
    }

    @Override
    public Page<ProductDto> getAllProductsByRegionId(int page, int size, int categoryId) {
        return productRepository.getProductsByRegionId(categoryId, PageRequest.of(page, size))
                .map(productMapper::toProductDto);
    }

    @Override
    public Page<ProductDto> getAllProductsBySubRegionId(int page, int size, int subRegionId) {
        return productRepository.getProductsBySubRegionId(subRegionId,PageRequest.of(page,size))
                .map(productMapper::toProductDto);
    }

    @Override
    @Transactional
    public boolean markProductAsPaid(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new GenericException("Product not found"));

        // Check if the product is already marked as paid
        boolean isPaid = product.getIsPaid() != null && product.getIsPaid();

        // If already paid, remove from paid list
        product.setIsPaid(!isPaid);

        productRepository.save(product);

        // Return whether the product was marked as paid
        return !isPaid;
    }



}
