package com.sibrahim.annoncify.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
@AllArgsConstructor @NoArgsConstructor @Builder @Getter @Setter @ToString
public class UserDto {

    private String name;
    private String phoneNumber;
    private List<ProductDto> productDtoList;
    private Date createDate;
    private Date updateDate;
}
