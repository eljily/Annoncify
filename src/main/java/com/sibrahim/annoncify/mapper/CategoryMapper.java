package com.sibrahim.annoncify.mapper;

import com.sibrahim.annoncify.dto.CategoryDto;
import com.sibrahim.annoncify.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryMapper {

    @Autowired
    private ProductMapper productMapper;

    public Category toCategory(CategoryDto categoryDto){
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setProductList(productMapper.toProducts(categoryDto.getProducts()));
        return category;
    }

    public CategoryDto toCategoryDto(Category category){
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setProducts(productMapper.toProductDtos(category.getProductList()));
        return categoryDto;
    }

    public List<CategoryDto> toCategoryDtos(List<Category> categories){
        if(categories!=null){
            return categories
                    .stream()
                    .map(this::toCategoryDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<Category> toCategories(List<CategoryDto> categories){
        if(categories!=null){
            return categories
                    .stream()
                    .map(this::toCategory)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
