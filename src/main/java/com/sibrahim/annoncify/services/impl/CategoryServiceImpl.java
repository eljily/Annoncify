package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.CategoryDto;
import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.mapper.CategoryMapper;
import com.sibrahim.annoncify.repository.CategoryRepository;
import com.sibrahim.annoncify.services.CategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public CategoryDto saveCategory(CategoryDto categoryDto) {
        Optional<Category> category1 = categoryRepository
                .findCategoryByName(categoryDto.getName());
        if (category1.isEmpty()){
            Category category = categoryMapper.toCategory(categoryDto);
            category.setCreateDate(LocalDateTime.now());
            category.setUpdateDate(LocalDateTime.now());
            return categoryMapper.toCategoryDto(categoryRepository
                    .save(category));
        }
        return new CategoryDto();
    }

    @Override
    public CategoryDto getCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()){
            return categoryMapper.toCategoryDto(category.get());
        }
        else {
            return new CategoryDto();
        }
    }

    @Override
    public List<CategoryDto> getAll() {
        return categoryMapper.toCategoryDtos(categoryRepository.findAll());
    }

    @Override
    public String deleteCategory(Long id) {
        categoryRepository.deleteById(id);
        return "Category deleted !:"+id;
    }

    @Override
    public Category fetchOrCreateDefault() {
        Category category = categoryRepository
                .findCategoryByName("other")
                .orElse(Category.builder()
                        .name("other")
                        .createDate(LocalDateTime.now())
                        .updateDate(LocalDateTime.now())
                        .build());
        if (category.getId()!=null){
            return category;
        }
        return categoryRepository.save(category);
    }
}
