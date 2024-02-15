package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductDto> getAllProducts();

    List<ProductDto> getProductsByUserId(Long id);

    Optional<ProductDto> getProductById(Long id);

    Optional<Product> getById(Long id);

    void deleteProduct(Long id);

    ProductDto saveProduct(ProductDto product);

    Product addProductWithImages(Product product, List<MultipartFile> imageFiles);

    String uploadImageToFirebase(MultipartFile imageFile);
}
