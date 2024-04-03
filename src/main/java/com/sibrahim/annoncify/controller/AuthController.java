package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.services.AuthService;
import com.sibrahim.annoncify.services.OtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService, OtpService otpService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseMessage> signup(@RequestBody RegisterDto registerDto) throws IOException {
            return ResponseEntity.ok(ResponseMessage.builder()
                            .status(HttpStatus.OK.value())
                            .message("User Registered Successfully")
                            .data(authService.registerUser(registerDto))
                    .build());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody AuthRequestDto authRequestDto) {
            return ResponseEntity.ok(authService.login(authRequestDto));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ResponseMessage> verify(@RequestBody OtpLoginDto otpLogin) {
        try {
            return ResponseEntity.ok(ResponseMessage.builder()
                    .data(authService.verify(otpLogin))
                    .status(HttpStatus.OK.value())
                    .build());
        } catch (Exception e) {
            log.error("ERROR WHILE TRYING TO VERIFY OTP,message:" + e.getMessage());
            return null;
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ResponseMessage> resendOtp(@RequestParam(name = "phoneNumber") String phoneNumber) {
        try {
            return ResponseEntity.ok(ResponseMessage.builder()
                    .data(authService.resendOtp(phoneNumber))
                    .status(HttpStatus.OK.value())
                    .build());
        } catch (Exception e) {
            log.error("ERROR WHILE TRYING TO RESEND  OTP,message:" + e.getMessage());
            return null;
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ResponseMessage> changePassword(@RequestBody ResetPasswordDto resetPasswordDto, @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(ResponseMessage.builder()
                        .message(authService.resetPassword(resetPasswordDto,userDetails))
                        .build());
    }
}
