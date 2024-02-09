package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.mapper.ProductMapper;
import com.sibrahim.annoncify.repository.ProductRepository;
import com.sibrahim.annoncify.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductMapper productMapper;


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
    public Optional<ProductDto> getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        return Optional.of(productMapper.toProductDto(product));
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        productRepository.deleteById(product.getId());
    }

    @Override
    public ProductDto saveProduct(ProductDto productDto) {
        Product product = productMapper.toProduct(productDto);
        if (product.getId()==null){
            return productMapper.toProductDto(productRepository.save(product));
        }
        else {
            product.setUpdateDate(new Date());
            return productMapper.toProductDto(productRepository.save(product));
        }
    }
}
