package com.sibrahim.annoncify.repository;

import com.sibrahim.annoncify.entity.Favorite;
import com.sibrahim.annoncify.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
