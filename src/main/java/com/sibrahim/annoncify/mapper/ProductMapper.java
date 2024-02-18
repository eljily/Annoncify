package com.sibrahim.annoncify.mapper;

import com.sibrahim.annoncify.dto.CategoryDto;
import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.dto.ProductRequestDto;
import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.services.CategoryService;
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

    @Autowired
    @Lazy
    private CategoryMapper categoryMapper;

    public ProductDto toProductDto(Product product){
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .createDate(product.getCreateDate())
                .updateDate(product.getUpdateDate())
                .images(imageMapper.toImageDtos(product.getImages()))
                .category(categoryMapper.toCategoryResponseDto(product.getCategory()))
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
                .category(categoryMapper.toCategory(productDto.getCategory()))
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
        if (productDtos!=null){
            return productDtos
                    .stream()
                    .map(this::toProduct)
                    .collect(Collectors.toList());
        }
        return List.of();
    }


}
