package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.dto.LoginDto;
import com.sibrahim.annoncify.dto.LoginResponseDto;
import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.dto.UserDto;
import com.sibrahim.annoncify.services.AuthService;
import com.sibrahim.annoncify.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody RegisterDto registerDto){
        try {
            return ResponseEntity.ok(authService.registerUser(registerDto));
        }catch (Exception e){
            log.error("ERROR WHILE TRYING TO SIGNUP,message:"+e.getMessage());
            return null;
        }

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto){
        try {
            return ResponseEntity.ok(authService.login(loginDto));
        }catch (Exception e){
            log.error("ERROR WHILE TRYING TO LOGIN,message:"+e.getMessage());
            return null;
        }

    }
}
