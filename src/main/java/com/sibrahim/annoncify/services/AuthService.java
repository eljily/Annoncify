package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.AuthRequestDto;
import com.sibrahim.annoncify.dto.AuthResponseDto;
import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.dto.UserDto;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto authRequestDto);

    public UserDto registerUser(RegisterDto registerDto);
    
}
