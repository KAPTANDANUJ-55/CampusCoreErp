package com.campuscore.authservice.dto;

import com.campuscore.authservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @Builder @AllArgsConstructor
public class UserSummaryDto {
    private String userId;
    private String fullName;
    private String email;
    private Role role;
    private String department;
    private boolean active;
}