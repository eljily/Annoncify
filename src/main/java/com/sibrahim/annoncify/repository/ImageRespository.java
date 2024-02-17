package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ImageRespository extends JpaRepository<Image,Long> {
    @Query("SELECT i FROM Image i WHERE i.imageUrl = :url")
    Optional<Image> findByUrl(String url);
}
