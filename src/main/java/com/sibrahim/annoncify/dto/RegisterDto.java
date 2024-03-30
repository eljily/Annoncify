package com.sibrahim.annoncify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class RegisterDto {
    private String name;
    private String phoneNumber;
    private String password;
    private String email;
}
