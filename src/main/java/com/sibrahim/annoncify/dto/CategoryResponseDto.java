package com.sibrahim.annoncify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CategoryResponseDto {
    private Long id;
    private String name;
    private Set<ProductCardInfoDto> products;
}
