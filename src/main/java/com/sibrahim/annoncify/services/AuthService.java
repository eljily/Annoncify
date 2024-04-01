package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.*;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto authRequestDto);

    RegisterDto registerUser(RegisterDto registerDto);

    String verify(OtpLoginDto otpLogin);

    String resendOtp(String phoneNumber);
}
