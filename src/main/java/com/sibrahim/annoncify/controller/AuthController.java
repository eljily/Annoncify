package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.dto.LoginDto;
import com.sibrahim.annoncify.dto.LoginResponseDto;
import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.dto.UserDto;
import com.sibrahim.annoncify.services.AuthService;
import com.sibrahim.annoncify.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody RegisterDto registerDto){
        return ResponseEntity.ok(authService.registerUser(registerDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok(authService.login(loginDto));
    }
}
