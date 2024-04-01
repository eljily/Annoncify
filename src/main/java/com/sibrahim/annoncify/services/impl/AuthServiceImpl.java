package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.exceptions.NotFoundException;
import com.sibrahim.annoncify.mapper.UserMapper;
import com.sibrahim.annoncify.repository.UserRepository;
import com.sibrahim.annoncify.security.JwtService;
import com.sibrahim.annoncify.services.AuthService;
import com.sibrahim.annoncify.services.OtpService;
import com.sibrahim.annoncify.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public AuthServiceImpl(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService, OtpService otpService, UserMapper userMapper, UserRepository userRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
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
            User userDetails = (User) authentication.getPrincipal();
            jwt = jwtService.generateToken(userDetails);
            authResponseDto.setJwt(jwt);
            authResponseDto.setUserId(userDetails.getId());
        }
        return authResponseDto;
    }


    @Override
    public RegisterDto registerUser(RegisterDto registerDto) {
       otpService.sendOtpMessageToUser(registerDto);
       return registerDto;
    }

    @Override
    public String verify(OtpLoginDto otpLogin) {
        log.info("Phone NUmber"+otpLogin.getPhoneNumber());
        User user = userRepository.findUserByPhoneNumber(otpLogin.getPhoneNumber())
                .orElseThrow(()->new NotFoundException("user not found!"));
        if (otpService.verifyOtp(user, otpLogin.getOtp())){
            return "Account Verified you can now login.";
        }
        return "Invalid Credentials";
    }

    public String resendOtp(String phoneNumber){
        User user = userRepository.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(()->new NotFoundException("User not found !"));
        return otpService.resendOtpMessageToUser(user);
    }
}

