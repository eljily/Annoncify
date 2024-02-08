package com.sibrahim.annoncify.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor @NoArgsConstructor @Builder @Getter @Setter @ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private LocalDate createDate;
    private LocalDate updateDate;
    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Product> products;
}
