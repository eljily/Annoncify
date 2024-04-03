package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    Optional<SubCategory> findSubCategoryByName(String name);
    List<SubCategory> findAllByCategoryId(Long categoryId);
}
