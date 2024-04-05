package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.SubCategoryDto;
import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.entity.SubCategory;
import com.sibrahim.annoncify.exceptions.GenericException;
import com.sibrahim.annoncify.exceptions.NotFoundException;
import com.sibrahim.annoncify.exceptions.SubCategoryException;
import com.sibrahim.annoncify.mapper.CategoryMapper;
import com.sibrahim.annoncify.repository.CategoryRepository;
import com.sibrahim.annoncify.repository.SubCategoryRepository;
import com.sibrahim.annoncify.services.CategoryService;
import com.sibrahim.annoncify.services.SubCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public SubCategoryServiceImpl(SubCategoryRepository subCategoryRepository, CategoryService categoryService, CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.subCategoryRepository = subCategoryRepository;

        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public SubCategoryDto createSubCategory(SubCategoryDto subCategory,Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new GenericException("Category Not Found Exception"));
        SubCategory subCategory1 = SubCategory.builder()
                .name(subCategory.getName())
                .category(category)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        return categoryMapper.toDTO(subCategoryRepository.save(subCategory1));
    }

    @Override
    public SubCategoryDto getSubcategory(Long id) {
        SubCategory subCategory = subCategoryRepository.findById(id)
                .orElseThrow(()->new NotFoundException("SubCategory Not Found With id : "+id));
        return categoryMapper.toDTO(subCategory);
    }

    @Override
    public List<SubCategoryDto> getAll() {
        return null;
    }

    @Override
    public String deleteSubCategory(Long id) {
        return null;
    }

    @Override
    public List<SubCategoryDto> getByCategoryId(Long id) {
        return categoryMapper.toSubCategoryDtos(subCategoryRepository.findAllByCategoryId(id));
    }

    @Override
    public SubCategory getByName(String name) {
        return subCategoryRepository
                .findSubCategoryByName(name)
                .orElseThrow(()->new SubCategoryException("sub category not found:"+name));
    }

    @Override
    @Transactional
    public SubCategory fetchOrCreateDefault() {
        Category category = categoryRepository.findCategoryByName("other").orElse(
                Category.builder()
                        .createDate(LocalDateTime.now())
                        .updateDate(LocalDateTime.now())
                        .name("other")
                        .build()
        );
        SubCategory subCategory = subCategoryRepository.findSubCategoryByName("other").orElse(
                SubCategory.builder()
                        .name("other")
                        .createDate(LocalDateTime.now())
                        .updateDate(LocalDateTime.now())
                        .build());
        if (category.getId() != null && subCategory.getId() == null) {
            log.info("CATEGORY 'OTHER' EXISTS AND SUBCATEGORY DOES NOT EXIST");
            subCategory.setCategory(category);
            log.info("------BEFORE-------");
            SubCategory subCategory1 = subCategoryRepository.save(subCategory);
            log.info(String.valueOf(subCategory1.getId()));
            log.info("------AFTER-------");
            return subCategory1;
        } else if (category.getId() == null && subCategory.getId() != null) {
            log.info("CATEGORY 'OTHER' DOES NOT EXIST BUT SUBCATEGORY EXISTS");
            category = categoryRepository.save(category);
            subCategory.setCategory(category);
            return subCategoryRepository.save(subCategory);
        } else if (category.getId() == null && subCategory.getId() == null) {
            log.info("CATEGORY 'OTHER' AND SUBCATEGORY DO NOT EXIST");
            category = categoryRepository.save(category);
            subCategory.setCategory(category);
            return subCategoryRepository.save(subCategory);
        } else {
            log.info("CATEGORY 'OTHER' AND SUBCATEGORY EXIST");
            return subCategory;
        }
    }

}
