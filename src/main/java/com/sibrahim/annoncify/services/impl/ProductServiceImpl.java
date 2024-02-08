package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.repository.ProductRepository;
import com.sibrahim.annoncify.services.ProductService;

import java.util.List;
import java.util.Optional;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return List.of();
        }

    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public void deleteProduct(Product product) {
        productRepository.deleteById(product.getId());
    }

    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
}
