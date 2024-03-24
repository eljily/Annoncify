package com.sibrahim.annoncify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductRequestDto {

    private String name;
    private String description;
    private Integer price;
    private List<MultipartFile> images= new ArrayList<>();
    private Long categoryId;

}
