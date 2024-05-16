package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.ImageDto;
import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.dto.ProductRequestDto;
import com.sibrahim.annoncify.entity.*;
import com.sibrahim.annoncify.entity.enums.ProductStatus;
import com.sibrahim.annoncify.exceptions.NotFoundException;
import com.sibrahim.annoncify.mapper.CategoryMapper;
import com.sibrahim.annoncify.mapper.ImageMapper;
import com.sibrahim.annoncify.mapper.ProductMapper;
import com.sibrahim.annoncify.repository.*;
import com.sibrahim.annoncify.services.CategoryService;
import com.sibrahim.annoncify.services.ProductService;
import com.sibrahim.annoncify.services.SubCategoryService;
import com.sibrahim.annoncify.services.UserService;
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
    public List<ProductDto> getProductsByUserId(Long id) {
        User user = userService.getById(id).orElseThrow(()->new NotFoundException("User not found"));
        List<Product> products = productRepository.findProductsByUserId(user.getId());
        return productMapper.toProductDtos(products);
    }

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
        Pageable pageable = PageRequest.of(0, 8); // Limit to 8 results
        return productRepository.findLastEightProductsByCategoryId(categoryId, pageable);
    }

}
