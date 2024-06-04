package com.sibrahim.annoncify.dto;

import com.sibrahim.annoncify.entity.User;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ProductDto {

    private Long id;
    private String name;
    private Integer price;
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;
    private List<ImageDto> images;
    private String subCategory;
    private String mark;
    private Long userId;
    private Long hit;
    private VendorDetails vendorDetails;
    private String region;
    private String subRegion;
    private Boolean isPaid;
//    private CategoryResponseDto category;

}
