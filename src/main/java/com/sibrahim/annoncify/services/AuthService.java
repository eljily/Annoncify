package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.*;

import java.io.IOException;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto authRequestDto);

    RegisterDto registerUser(RegisterDto registerDto) throws IOException;

    String verify(OtpLoginDto otpLogin);

    String resendOtp(String phoneNumber);
}
