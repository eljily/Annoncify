package com.sibrahim.annoncify.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "products")
@AllArgsConstructor @NoArgsConstructor @Builder @Getter @Setter @ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private LocalDate createDate;
    private LocalDate updateDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}
