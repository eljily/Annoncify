package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.*;

public interface AuthService {

    AuthResponseDto login(AuthRequestDto authRequestDto);

    ResponseMessage registerUser(RegisterDto registerDto);

    String verify(OtpLoginDto otpLogin);
}
