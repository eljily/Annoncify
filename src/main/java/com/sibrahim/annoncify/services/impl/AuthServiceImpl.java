package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.LoginDto;
import com.sibrahim.annoncify.dto.LoginResponseDto;
import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.dto.UserDto;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.entity.enums.RoleEnum;
import com.sibrahim.annoncify.mapper.UserMapper;
import com.sibrahim.annoncify.security.JwtService;
import com.sibrahim.annoncify.services.AuthService;
import com.sibrahim.annoncify.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    @Override
    public LoginResponseDto login(LoginDto loginDto) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        String jwt = "bad request";
        // Authenticate the user using Spring Security's authentication manager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getPhoneNumber(), loginDto.getPassword()));

        // If authentication is successful, generate and set the JWT token
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            jwt = jwtService.generateToken(userDetails);
            loginResponseDto.setJwt(jwt);
        }
        return loginResponseDto;
    }


    @Override
    public UserDto registerUser(RegisterDto registerDto) {
       return userService.saveUser(registerDto);
    }
}
