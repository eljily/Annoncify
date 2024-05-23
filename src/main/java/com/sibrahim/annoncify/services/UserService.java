package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.entity.User;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserService {

    RegisterDto saveUser(RegisterDto registerDto,Long id) throws IOException;

    Page<UserDto> getAllUsers(int page,int size);

    Optional<User> getUserByPhoneNumber(String phoneNumber);

//    ProductDto addProduct(ProductDto productDto);

    void deleteProduct(Long id);

    RegisterDto getUserById(Long id);

    Optional<User> getById(Long id);

    List<ProductDto> getProducts();
}
