package com.sibrahim.annoncify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class OtpLoginDto {
    private String phoneNumber;
    private String otp;
}
