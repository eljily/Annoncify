package com.sibrahim.annoncify.dto;

import lombok.*;

@AllArgsConstructor @NoArgsConstructor @Builder @Getter @Setter
public class VendorDetails {
    private String name;
    private String phoneNumber;
    private String profileUrl;
    private Long id;
}
