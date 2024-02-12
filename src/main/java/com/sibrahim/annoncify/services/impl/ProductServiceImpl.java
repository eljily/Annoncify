package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.mapper.ProductMapper;
import com.sibrahim.annoncify.repository.ProductRepository;
import com.sibrahim.annoncify.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

}
