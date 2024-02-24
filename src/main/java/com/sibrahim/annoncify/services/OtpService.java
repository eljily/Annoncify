package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.entity.Otp;
import com.sibrahim.annoncify.entity.User;

public interface OtpService {
    Otp save(Otp otp);

    String sendOtpMessageToUser(RegisterDto user);

    boolean verifyOtp(User user, String code);
}
