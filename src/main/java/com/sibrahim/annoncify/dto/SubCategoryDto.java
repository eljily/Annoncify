package com.sibrahim.annoncify.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubCategoryDto {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;
    private String nameAr;
}
