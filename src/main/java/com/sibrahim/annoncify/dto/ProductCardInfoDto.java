package com.sibrahim.annoncify.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@AllArgsConstructor @NoArgsConstructor @Builder @Data
public class ProductCardInfoDto {
    private Long id;
    private String name;
    private Integer price;
    private List<ImageDto> images;
    private String region;
    private String subRegion;
    private Long hit;
    private Boolean isPaid;
}
