package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
