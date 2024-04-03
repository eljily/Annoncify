package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.CategoryDto;
import com.sibrahim.annoncify.dto.SubCategoryDto;
import com.sibrahim.annoncify.entity.SubCategory;

import java.util.List;

public interface SubCategoryService {
    SubCategoryDto createSubCategory(SubCategoryDto category,Long categoryId);
    SubCategoryDto getSubcategoryCategory(Long id);
    List<SubCategoryDto> getAll();
    String deleteSubCategory(Long id);
    List<SubCategoryDto> getByCategoryId(Long id);
    SubCategory getByName(String name);
    SubCategory fetchOrCreateDefault();
}
