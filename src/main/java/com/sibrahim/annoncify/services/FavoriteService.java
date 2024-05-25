package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Product;

import java.util.List;

public interface FavoriteService {
    void addProductToFavorites(Long userId, Long productId);
    List<ProductDto> getUserFavoriteProducts(Long userId);
    void removeProductFromFavorites(Long userId, Long productId);
}
