package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Image;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.mapper.ProductMapper;
import com.sibrahim.annoncify.repository.ImageRespository;
import com.sibrahim.annoncify.repository.ProductRepository;
import com.sibrahim.annoncify.services.FirebaseStorageService;
import com.sibrahim.annoncify.services.ProductService;
import com.sibrahim.annoncify.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ImageRespository imageRespository;
    private final FirebaseStorageService firebaseStorageService;
    @Lazy
    @Autowired
    private UserService userService;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, ImageRespository imageRespository, FirebaseStorageService firebaseStorageService) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.imageRespository = imageRespository;
        this.firebaseStorageService = firebaseStorageService;
    }


    @Override
    public List<ProductDto> getAllProducts() {
        try {
            return productMapper.toProductDtos(productRepository.findAll());
        }catch (Exception e){
            System.out.println(e.getMessage());
            return List.of();
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
            Product savedProduct = productRepository.save(product);

            // Upload images to Firebase Storage and save their URLs to the database
            List<String> imageUrls = imageFiles.stream()
                    .map(this::uploadImageToFirebase)
                    .toList();

            for (String imageUrl : imageUrls) {
                Image image = new Image();
                image.setImageUrl(imageUrl);
                image.setProduct(savedProduct);

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
            return firebaseStorageService.uploadImage(imageFile);
        }catch (Exception e){
            log.error("ERROR WHILE TRYING TO UPLOAD IMAGE TO FIREBASE,message:",e);
            return null;
        }

    }

}
