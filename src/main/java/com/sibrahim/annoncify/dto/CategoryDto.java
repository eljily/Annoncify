package com.sibrahim.annoncify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CategoryDto {
    private Long id;
    private String name;
    private List<SubCategoryDto> subCategories;
    private String imageUrl;
}
