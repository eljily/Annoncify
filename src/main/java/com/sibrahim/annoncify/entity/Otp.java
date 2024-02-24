package com.sibrahim.annoncify.entity;

import com.sibrahim.annoncify.entity.enums.OtpStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@AllArgsConstructor @NoArgsConstructor @Builder @Data
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdDatetime;

    private LocalDateTime updatedDatetime;

    private String code;

    private LocalDateTime expiryDatetime;

    @Enumerated(EnumType.STRING)
    private OtpStatus status;
}
