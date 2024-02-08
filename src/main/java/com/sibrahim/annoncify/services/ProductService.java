package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    List<Product> getAllProducts();
    Optional<Product> getProductById(Long id);
    void deleteProduct(Product product);
    Product saveProduct(Product product);

}
