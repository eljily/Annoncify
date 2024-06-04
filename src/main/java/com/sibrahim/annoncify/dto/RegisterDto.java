package com.sibrahim.annoncify.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter @Builder
public class RegisterDto {
    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String address;
    private Date birthDate;
    private String phoneNumber;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    private MultipartFile profilePhoto;
    private String profileUrl;
    private String whatsAppNumber;
}
