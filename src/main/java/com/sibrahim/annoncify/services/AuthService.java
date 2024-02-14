package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.LoginDto;
import com.sibrahim.annoncify.dto.LoginResponseDto;
import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.dto.UserDto;

public interface AuthService {

    LoginResponseDto login(LoginDto loginDto);

    public UserDto registerUser(RegisterDto registerDto);
    
}
