package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp,Long> {
    Optional<Otp> findByUserId(Long id);
}
