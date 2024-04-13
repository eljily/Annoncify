package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.exceptions.GenericException;
import com.sibrahim.annoncify.exceptions.NotFoundException;
import com.sibrahim.annoncify.exceptions.UserAlreadyExistException;
import com.sibrahim.annoncify.mapper.UserMapper;
import com.sibrahim.annoncify.repository.UserRepository;
import com.sibrahim.annoncify.security.JwtService;
import com.sibrahim.annoncify.services.AuthService;
import com.sibrahim.annoncify.services.OtpService;
import com.sibrahim.annoncify.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService, OtpService otpService, UserMapper userMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        AuthResponseDto authResponseDto = new AuthResponseDto();
        String jwt = "bad request";

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDto.getPhoneNumber(), authRequestDto.getPassword()));

        if (authentication.isAuthenticated() && authentication.getPrincipal() != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            log.error("user details................");
            log.error(userDetails.getUsername());
            log.error(userDetails.getPassword());
            jwt = jwtService.generateToken(userDetails);
            User user = (User) userDetails;
            authResponseDto.setUserId(user.getId());
            authResponseDto.setJwt(jwt);
        }
        return authResponseDto;
    }


    @Override
    public RegisterDto registerUser(RegisterDto registerDto) throws IOException {
        //otpService.sendOtpMessageToUser(registerDto);
        Optional<User> user = userRepository.findUserByPhoneNumber(registerDto.getPhoneNumber());
        if (user.isPresent()) {
            throw new UserAlreadyExistException("User Already Exist :" + registerDto.getPhoneNumber());
        }
        return userService.saveUser(registerDto,null);
    }

    @Override
    public String verify(OtpLoginDto otpLogin) {
        log.info("Phone NUmber" + otpLogin.getPhoneNumber());
        User user = userRepository.findUserByPhoneNumber(otpLogin.getPhoneNumber())
                .orElseThrow(() -> new NotFoundException("user not found!"));
        if (otpService.verifyOtp(user, otpLogin.getOtp())) {
            return "Account Verified you can now login.";
        }
        return "Invalid Credentials";
    }

    public String resendOtp(String phoneNumber) {
        User user = userRepository.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundException("User not found !"));
        return otpService.resendOtpMessageToUser(user);
    }

    @Override
    public String resetPassword(ResetPasswordDto resetPasswordDto, UserDetails userDetails) {
        log.error("first log..............");

        if (resetPasswordDto.getRetypedPassword()!=null){
            if (!Objects.equals(resetPasswordDto.getNewPassword(), resetPasswordDto.getRetypedPassword())){
                throw new GenericException("two password not matching");
            }
        }

        if (userDetails == null) {
            throw new GenericException("User not authenticated");
        }

        log.error("userName :" + userDetails.getUsername());

        // Retrieve the user from the database based on the username
        User user = userRepository.findUserByPhoneNumber(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the old password matches
        if (passwordEncoder.matches(resetPasswordDto.getOldPassword(), user.getPassword())) {
            // Update the password
            user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
            userRepository.save(user); // Make sure to save the updated user entity
            return "Password updated successfully";
        } else {
            throw new BadCredentialsException("Old password does not match");
        }
    }

}

