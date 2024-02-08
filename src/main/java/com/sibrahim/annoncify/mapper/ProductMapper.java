package com.sibrahim.annoncify.mapper;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public ProductDto toProductDto(Product product){
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .createDate(product.getCreateDate())
                .updateDate(product.getUpdateDate())
                .user(product.getUser())
                .build();
    }

    public Product toProduct(ProductDto productDto){
        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .createDate(productDto.getCreateDate())
                .updateDate(productDto.getUpdateDate())
                .user(productDto.getUser())
                .build();
    }

    public List<ProductDto> toProductDtos(List<Product> products){
        return products
                .stream()
                .map(this::toProductDto)
                .collect(Collectors.toList());
    }

    public List<Product> toProducts(List<ProductDto> productDtos){
        return productDtos
                .stream()
                .map(this::toProduct)
                .collect(Collectors.toList());
    }
}
