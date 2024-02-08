package com.sibrahim.annoncify.entity;

import jakarta.persistence.*;
import lombok.*;

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
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
