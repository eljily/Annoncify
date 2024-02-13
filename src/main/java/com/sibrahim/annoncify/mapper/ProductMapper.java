package com.sibrahim.annoncify.mapper;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ProductMapper {

    @Autowired
    @Lazy
    private ImageMapper imageMapper;

    @Autowired
    @Lazy
    private UserMapper userMapper;

    public ProductDto toProductDto(Product product){
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .createDate(product.getCreateDate())
                .updateDate(product.getUpdateDate())
                .images(imageMapper.toImageDtos(product.getImages()))
                .build();
    }

    public Product toProduct(ProductDto productDto){
        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .images(imageMapper.toImages(productDto.getImages()))
                .createDate(productDto.getCreateDate())
                .updateDate(productDto.getUpdateDate())
                .build();
    }

    public List<ProductDto> toProductDtos(List<Product> products){
        if(products!=null){
            return products
                    .stream()
                    .map(this::toProductDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<Product> toProducts(List<ProductDto> productDtos){
        return productDtos
                .stream()
                .map(this::toProduct)
                .collect(Collectors.toList());
    }
}
