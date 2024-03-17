package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.CategoryDto;
import com.sibrahim.annoncify.dto.ImageDto;
import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.dto.ProductRequestDto;
import com.sibrahim.annoncify.entity.Image;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.entity.enums.ProductStatus;
import com.sibrahim.annoncify.mapper.CategoryMapper;
import com.sibrahim.annoncify.mapper.ImageMapper;
import com.sibrahim.annoncify.mapper.ProductMapper;
import com.sibrahim.annoncify.repository.ImageRespository;
import com.sibrahim.annoncify.repository.ProductRepository;
import com.sibrahim.annoncify.services.CategoryService;
import com.sibrahim.annoncify.services.ProductService;
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
    @Lazy
    @Autowired
    private UserService userService;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, ImageRespository imageRespository, ImageServiceImpl imageService, CategoryService categoryService, ImageMapper imageMapper, CategoryMapper categoryMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.imageRespository = imageRespository;
        this.imageService = imageService;
        this.categoryService = categoryService;
        this.imageMapper = imageMapper;
        this.categoryMapper = categoryMapper;
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
    public Page<ProductDto> getAllProducts(int page, int size, int categoryId) {
        try {
            return productRepository.findByCategory_Id(categoryId, PageRequest.of(page, size))
                    .map(productMapper::toProductDto);
        } catch (Exception e) {
            log.error("Error occurred while trying to fetch page of products by category", e);
            return null;
        }
    }

    @Override
    public List<ProductDto> getProductsByUserId(Long id) {
        User user = userService.getById(id).orElseThrow();
        List<Product> products = productRepository.findProductsByUserId(user.getId());
        return productMapper.toProductDtos(products);
    }

    @Override
    public Optional<ProductDto> getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
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

    @Override
    public ProductDto saveProduct(ProductDto productDto) {
        Product product = productMapper.toProduct(productDto);

        if (product.getId() == null) {
            // This is a new product, save it
            product.setProductStatus(ProductStatus.PENDING);
            product.setCreateDate(new Date());
            product.setUpdateDate(new Date());
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            // Extract user details from Authentication
                User user = (User) authentication.getPrincipal();
                product.setUser(user);
            return productMapper.toProductDto(productRepository.save(product));
        } else {
            // This is an update, merge the existing product with the new data
            Product dbProduct = productRepository.findById(product.getId()).orElse(null);

            if (dbProduct != null) {
                // Update only the fields that are not null in the incoming productDto
                if (productDto.getName() != null) {
                    dbProduct.setName(productDto.getName());
                }
                if (productDto.getPrice() != null) {
                    dbProduct.setPrice(productDto.getPrice());
                }
               if (productDto.getDescription() !=null){
                   dbProduct.setDescription(productDto.getDescription());
               }
                dbProduct.setUpdateDate(new Date());
                // Save the updated product
                return productMapper.toProductDto(productRepository.save(dbProduct));
            } else {
                // Handle the case where the product with the given ID is not found
                return null;
            }
        }
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

    public String uploadImageToFirebase(MultipartFile imageFile){
        try {
            // Upload the image to Firebase Storage and return the URL
            log.info("UPLOADING IMAGE TO FIREBASE........");
            return imageService.upload(imageFile);
        }catch (Exception e){
            log.error("WHILE TRYING TO UPLOAD IMAGE TO FIREBASE,message:"+e.getMessage());
            return null;
        }

    }

    @Override
    public ProductDto addProduct(ProductRequestDto productRequestDto) {
        ProductDto productDto = new ProductDto();
        if (productRequestDto.getCategoryId()!=null){
            CategoryDto categoryDto = categoryService
                    .getCategory(productRequestDto.getCategoryId());
//            productDto.setCategory(categoryMapper.toCategoryResponseDto(categoryDto));
        }
        productDto.setName(productRequestDto.getName());
        productDto.setPrice(productRequestDto.getPrice());
        productDto.setDescription(productRequestDto.getDescription());

        Product product = productMapper.toProduct(productDto);

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

        productDto.setId(savedProduct.getId());

        List<String> imageUrls = productRequestDto.getImages().stream()
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
        productDto.setImages(imageDtos);
        productDto.setCreateDate(savedProduct.getCreateDate());
        productDto.setUpdateDate(savedProduct.getUpdateDate());
        return productDto;
    }

}
