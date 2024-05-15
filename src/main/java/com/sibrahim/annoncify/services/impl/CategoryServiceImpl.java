package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.CategoryDto;
import com.sibrahim.annoncify.dto.CategoryResponseDto;
import com.sibrahim.annoncify.dto.ProductCardInfoDto;
import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.mapper.CategoryMapper;
import com.sibrahim.annoncify.mapper.ProductMapper;
import com.sibrahim.annoncify.repository.CategoryRepository;
import com.sibrahim.annoncify.services.CategoryService;
import com.sibrahim.annoncify.services.ProductService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final ProductService productService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper, ProductMapper productMapper,@Lazy ProductService productService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.productService = productService;
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

    public List<CategoryResponseDto> getAllCategoriesWithLastEightProducts() {
        List<Category> categories = categoryRepository.findAll();

        List<CategoryResponseDto> categoryResponseDtos = new ArrayList<>();
        for (Category category : categories) {
            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            categoryResponseDto.setId(category.getId());
            categoryResponseDto.setName(category.getName());

            List<Product> products = productService.findLastEightProductsByCategoryId(category.getId());
            Set<ProductCardInfoDto> productDtos = products.stream()
                    .map(productMapper::toCardInfoDto)
                    .collect(Collectors.toSet());

            categoryResponseDto.setProducts(productDtos);
            categoryResponseDtos.add(categoryResponseDto);
        }

        return categoryResponseDtos;
    }
}
