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
import com.sibrahim.annoncify.repository.CategoryRepository;
import com.sibrahim.annoncify.repository.ImageRespository;
import com.sibrahim.annoncify.repository.ProductRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ImageRespository imageRespository;
    private final ImageServiceImpl imageService;
    private final CategoryService categoryService;
    private final ImageMapper imageMapper;
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final SubCategoryService subCategoryService;
    @Lazy
    @Autowired
    private UserService userService;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, ImageRespository imageRespository, ImageServiceImpl imageService, CategoryService categoryService, ImageMapper imageMapper, CategoryMapper categoryMapper, CategoryRepository categoryRepository, SubCategoryService subCategoryService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.imageRespository = imageRespository;
        this.imageService = imageService;
        this.categoryService = categoryService;
        this.imageMapper = imageMapper;
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
        this.subCategoryService = subCategoryService;
    }


    @Override
    public Page<ProductDto> getAllProducts(int page,int size) {
        try {
            return productRepository
                    .findAll(PageRequest.of(page,size))
                    .map(productMapper::toProductDto);
        }catch (Exception e){
            log.error("OCCURRED WHILE TRYING TO FETCH PAGE OF PRODUCT",e);
            return null;
        }

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
        productRepository.deleteById(product.getId());
    }

    public Product addProductWithImages(Product product, List<MultipartFile> imageFiles) {
        try {
            // Save the product to the database
            product.setCreateDate(new Date());
            Product savedProduct = productRepository.save(product);

            // Upload images to Firebase Storage and save their URLs to the database
            List<String> imageUrls = imageFiles.stream()
                    .map(this::uploadImageToFirebase)
                    .toList();

            for (String imageUrl : imageUrls) {
                Image image = new Image();
                image.setImageUrl(imageUrl);
                image.setProduct(savedProduct);
                image.setCreateDate(LocalDateTime.now());
                image.setUpdateDate(LocalDateTime.now());
                imageRespository.save(image);
            }

            return savedProduct;
        }catch (Exception e){
            log.error("ERROR WHILE TRYING TO SAVE PRODUCT WITH IMAGES,message:"+e.getMessage());
            return null;
        }

    }

    @Override
    public ProductDto addProduct(ProductRequestDto productRequestDto) {
        Product product = new Product();

        product.setName(productRequestDto.getName());
        product.setPrice(productRequestDto.getPrice());
        product.setDescription(productRequestDto.getDescription());

            SubCategory subCategory;
            if (productRequestDto.getSubCategory()!=null){
                subCategory = subCategoryService
                        .getByName(productRequestDto.getSubCategory());
            }
            else {
                subCategory = subCategoryService.fetchOrCreateDefault();
            }
            product.setSubCategory(subCategory);

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // Extract user details from Authentication
        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            product.setUser(user);
        } else {
            // Set user to null if the principal is not of type User
            product.setUser(null);
        }
        product.setProductStatus(ProductStatus.PENDING);
        product.setCreateDate(new Date());
        product.setUpdateDate(new Date());
        Product savedProduct = productRepository.save(product);

//        product.setId(savedProduct.getId());

        List<String> imageUrls = productRequestDto.getImages().parallelStream() // Used parallelStream() for concurrent processing
                .map(this::uploadImageToFirebase)
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
//        productDto.setImages(imageDtos);
//        productDto.setCreateDate(savedProduct.getCreateDate());
//        productDto.setUpdateDate(savedProduct.getUpdateDate());
        return productMapper.toProductDto(savedProduct);
    }
    public String uploadImageToFirebase(MultipartFile imageFile) {
        try {
            // Upload the image to Firebase Storage and return the URL
            log.info("UPLOADING IMAGE TO FIREBASE: {}", imageFile.getOriginalFilename());
            String imageUrl = imageService.upload(imageFile);
            log.info("UPLOAD SUCCESSFUL: {} - URL: {}", imageFile.getOriginalFilename(), imageUrl);
            return imageUrl;
        } catch (Exception e) {
            log.error("FAILED TO UPLOAD IMAGE TO FIREBASE: {} - Error: {}", imageFile.getOriginalFilename(), e.getMessage());
            return null;
        }
    }

}
