package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<ProductDto> getAllProducts();
    Optional<ProductDto> getProductById(Long id);
    void deleteProduct(Long id);
    ProductDto saveProduct(ProductDto product);

}
