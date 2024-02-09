package com.sibrahim.annoncify.dto;

import com.sibrahim.annoncify.entity.User;
import lombok.*;

import java.time.LocalDate;
@AllArgsConstructor @NoArgsConstructor @Builder @Getter @Setter @ToString @EqualsAndHashCode
public class ProductDto {

    private Long id;
    private String name;
    private Integer price;
    private String description;
    private LocalDate createDate;
    private LocalDate updateDate;

}
