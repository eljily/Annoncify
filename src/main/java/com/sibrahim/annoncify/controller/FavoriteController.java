package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.dto.ResponseMessage;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.exceptions.GenericException;
import com.sibrahim.annoncify.services.FavoriteService;
import com.sibrahim.annoncify.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final UserService userService;
    private final FavoriteService favoriteService;

    public FavoriteController(UserService userService, FavoriteService favoriteService) {
        this.userService = userService;
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<ResponseMessage> addProductToFavorites(@PathVariable Long productId, Authentication authentication) {
        String username = authentication.getName();
        // Assuming username is the user's phone number
        Long userId = userService.getUserByPhoneNumber(username).get().getId();
        favoriteService.addProductToFavorites(userId, productId);
        return ResponseEntity.ok(ResponseMessage.builder()
                        .status(200)
                        .message("Added to favorite Successfully")
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getUserFavoriteProducts(Authentication authentication) {
        String username = authentication.getName();
        Long userId = userService.getUserByPhoneNumber(username).orElseThrow(() -> new GenericException("User not found")).getId();
        List<ProductDto> favoriteProducts = favoriteService.getUserFavoriteProducts(userId);
        return ResponseEntity.ok(ResponseMessage.builder()
                        .status(200)
                        .message("User favorites products Retrieved Successfully")
                        .data(favoriteProducts)
                .build());
    }
}

