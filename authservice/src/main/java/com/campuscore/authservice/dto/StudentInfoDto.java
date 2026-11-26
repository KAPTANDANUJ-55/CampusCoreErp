package com.campuscore.authservice.dto;

public record StudentInfoDto(
        String userId,
        String fullName,
        String department,
        boolean isActive
) {}