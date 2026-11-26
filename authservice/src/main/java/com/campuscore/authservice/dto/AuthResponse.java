package com.campuscore.authservice.dto;

import com.campuscore.authservice.entity.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse implements LoginResponse,RegisterResponse {
    private String token;
    private String userId;
    private String fullName;
    private Role role;
    private String department;

    @Override
    public String getMessage() {
        return (token==null) ? RegisterResponse.super.getMessage() :LoginResponse.super.message();
    }


}