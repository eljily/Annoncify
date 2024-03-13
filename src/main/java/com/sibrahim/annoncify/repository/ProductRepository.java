package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Long> {
    @Query("SELECT p FROM Product p WHERE p.user.id = :userId")
    List<Product> findProductsByUserId(@Param("userId") Long userId);
    Page<Product> findByCategory_Id(int categoryId, Pageable pageable);
}
