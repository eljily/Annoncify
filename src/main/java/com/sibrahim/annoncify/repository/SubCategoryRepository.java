package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    Optional<SubCategory> findSubCategoryByName(String name);

    List<SubCategory> findAllByCategoryId(Long categoryId);

    Optional<SubCategory> findByCategoryAndName(Category category, String name);

    @Query("SELECT s FROM SubCategory s WHERE s.category.name = :categoryName AND s.name = :subCategoryName")
    Optional<SubCategory> findByCategoryNameAndSubCategoryName(String categoryName, String subCategoryName);
}
