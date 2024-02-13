package com.sibrahim.annoncify.dto;

import lombok.*;

@AllArgsConstructor @NoArgsConstructor @Builder @Getter @Setter @ToString
public class ImageDto {
    private Long id;
    private String imageUrl;
}
