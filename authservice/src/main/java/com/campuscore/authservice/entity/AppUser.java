package com.campuscore.authservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // This Id is Either For Student Or For Staff etc

    @Column(nullable = false, unique = true)
    private String userId; // Student Enrollment (e.g. 250160124) OR Emp ID (e.g. EMP101 / PRINCIPAL_01)

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String department;

    private boolean isActive =  false;

}
