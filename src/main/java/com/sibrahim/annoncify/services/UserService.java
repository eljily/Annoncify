package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    LoginResponseDto login(LoginDto loginDto);
    UserDto saveUser(RegisterDto registerDto);
    List<UserDto> getAllUsers();
    Optional<UserDto> getUserByPhoneNumber(String phoneNumber);
    ProductDto addProduct(ProductDto productDto);
    void deleteProduct(Long id);
    UserDto getUserById(Long id);
}
