package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.LoginDto;
import com.sibrahim.annoncify.dto.LoginResponseDto;
import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.dto.UserDto;
import com.sibrahim.annoncify.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    LoginResponseDto login(LoginDto loginDto);
    UserDto saveUser(RegisterDto registerDto);
    List<UserDto> getAllUsers();
    Optional<UserDto> getUserByPhoneNumber(String phoneNumber);
}
