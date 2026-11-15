package com.campuscore.authservice.dto;

import com.campuscore.authservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class AuthResponse implements LoginResponse {
    private String token;
    private String userId;
    private String fullName;
    private Role role;
    private String department;

}