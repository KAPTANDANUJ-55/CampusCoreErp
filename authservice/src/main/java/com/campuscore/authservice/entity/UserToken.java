package com.campuscore.authservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "user_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false)
    private String token;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private LocalDateTime expiryDate;
}
