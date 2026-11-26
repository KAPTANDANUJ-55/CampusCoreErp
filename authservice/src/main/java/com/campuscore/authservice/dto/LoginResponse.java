package com.campuscore.authservice.dto;


import com.campuscore.authservice.entity.Role;

public interface LoginResponse {
    default String message(){
        return "Login Successful";
    }
    Role getRole();
    String getDepartment();
}
