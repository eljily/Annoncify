package com.sibrahim.annoncify.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    public String toString() {
        return "Image(id=" + this.getId()
                + ", imageUrl=" + this.getImageUrl()
                + ", createDate=" + this.getCreateDate()
                + ", updateDate=" + this.getUpdateDate()
//                + ", product=" + this.getProduct()
                + ")";
    }
}
