package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.entity.Category;

import java.util.Optional;

public interface CategoryService {

    Category saveCategory(Category category);
    Optional<Category> getCategory(Long id);
    String deleteCategory(Long id);
}
