package com.campuscore.authservice.dto;


public interface LoginResponse {
    default String message(){
        return "Login Successful";
    }
}
