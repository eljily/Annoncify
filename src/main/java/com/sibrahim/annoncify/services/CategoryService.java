package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.CategoryDto;
import com.sibrahim.annoncify.dto.CategoryResponseDto;
import com.sibrahim.annoncify.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    CategoryDto saveCategory(CategoryDto category);
    CategoryDto getCategory(Long id);
    List<CategoryDto> getAll();
    String deleteCategory(Long id);
    Category fetchOrCreateDefault();
    List<CategoryResponseDto> getAllCategoriesWithLastEightProducts();
}
