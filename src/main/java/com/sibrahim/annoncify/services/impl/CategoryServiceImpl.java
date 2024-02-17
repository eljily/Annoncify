package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.repository.CategoryRepository;
import com.sibrahim.annoncify.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category saveCategory(Category category) {
        Category savedCategory =categoryRepository.save(category);
        return savedCategory;
    }

    @Override
    public Optional<Category> getCategory(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public String deleteCategory(Long id) {
        categoryRepository.deleteById(id);
        return "Category deleted !:"+id;
    }
}
