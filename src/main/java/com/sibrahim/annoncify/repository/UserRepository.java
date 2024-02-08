package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
