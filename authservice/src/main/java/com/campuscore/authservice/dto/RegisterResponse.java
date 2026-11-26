package com.campuscore.authservice.dto;


import com.campuscore.authservice.entity.Role;

public interface RegisterResponse {

    String getFullName();
    default String getMessage() {
        return "User: " + getFullName() + " is registered successfully!";
    }

     Role getRole();
    String getDepartment();

}
