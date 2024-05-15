package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.dto.CategoryResponseDto;
import com.sibrahim.annoncify.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findCategoryByName(String name);
    @Query("SELECT c FROM Category c JOIN FETCH c.subCategories " +
            "sc JOIN FETCH sc.productList p ORDER BY c.id")
    List<Category> findAllCategoriesWithLastEightProducts();
}
