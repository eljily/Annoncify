package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.security.JwtService;
import com.sibrahim.annoncify.services.AuthService;
import com.sibrahim.annoncify.services.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        AuthResponseDto authResponseDto = new AuthResponseDto();
        String jwt = "bad request";
        // Authenticate the user using Spring Security's authentication manager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDto.getPhoneNumber(), authRequestDto.getPassword()));

        // If authentication is successful, generate and set the JWT token
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            jwt = jwtService.generateToken(userDetails);
            authResponseDto.setJwt(jwt);
        }
        return authResponseDto;
    }


    @Override
    public ResponseMessage registerUser(RegisterDto registerDto) {
       return userService.saveUser(registerDto);
    }
}
