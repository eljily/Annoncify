package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRespository extends JpaRepository<Image,Long> {
}
