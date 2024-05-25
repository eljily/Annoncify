package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Favorite;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.exceptions.GenericException;
import com.sibrahim.annoncify.mapper.ProductMapper;
import com.sibrahim.annoncify.repository.FavoriteRepository;
import com.sibrahim.annoncify.repository.ProductRepository;
import com.sibrahim.annoncify.repository.UserRepository;
import com.sibrahim.annoncify.services.FavoriteService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository, UserRepository userRepository, ProductRepository productRepository, ProductMapper productMapper) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public void addProductToFavorites(Long userId, Long productId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new GenericException("User not found"));
        Product product = productRepository.findById(productId).orElseThrow(() -> new GenericException("Product not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);

        favoriteRepository.save(favorite);
    }

    public List<ProductDto> getUserFavoriteProducts(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new GenericException("User not found"));
        return favoriteRepository.findByUser(user).stream()
                .map(Favorite::getProduct)
                .map(productMapper::toProductDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeProductFromFavorites(Long userId, Long productId) {
        favoriteRepository.deleteByUserIdAndProductId(userId, productId);
    }
}

