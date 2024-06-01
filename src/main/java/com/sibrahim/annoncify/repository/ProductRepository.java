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

    @Query("SELECT p FROM Product p WHERE p.subCategory.id = :categoryId ORDER BY p.createDate DESC")
    Page<Product> findBySubCategory_Id(@Param("categoryId") int categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.createDate DESC")
    Page<Product> findAllOrderedByCreateDateDesc(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.subCategory.category.id = :categoryId ORDER BY p.createDate DESC")
    List<Product> findLastEightProductsByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.subCategory.category.id = :categoryId ORDER BY p.createDate DESC")
    Page<Product> findAllByCategoryId(@Param("categoryId") int categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% ORDER BY p.createDate DESC")
    Page<Product> getProductsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.subRegion.region.id = :regionId ORDER BY p.createDate DESC")
    Page<Product> getProductsByRegionId(@Param("regionId") int regionId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.subRegion.id = :subRegionId ORDER BY p.createDate DESC")
    Page<Product> getProductsBySubRegionId(@Param("subRegionId") int subRegionId, Pageable pageable);

}
