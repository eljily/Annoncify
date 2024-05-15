package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.user.id = :userId")
    List<Product> findProductsByUserId(@Param("userId") Long userId);

    //    Page<Product> findByCategory_Id(int categoryId, Pageable pageable);
    @Query("SELECT p FROM Product p WHERE p.subCategory.id = :categoryId ORDER BY p.createDate DESC")
    Page<Product> findBySubCategory_Id(@Param("categoryId") int categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.createDate DESC")
    Page<Product> findAllOrderedByCreateDateDesc(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.subCategory.category.id = :categoryId ORDER BY p.createDate DESC")
    List<Product> findLastEightProductsByCategoryId(Long categoryId, Pageable pageable);
}
