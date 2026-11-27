package com.campuscore.authservice.controller;

import com.campuscore.authservice.dto.*;
import com.campuscore.authservice.entity.AppUser;
import com.campuscore.authservice.entity.Role;
import com.campuscore.authservice.repository.UserRepo;
import com.campuscore.authservice.service.LoginService;
import com.campuscore.authservice.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("api/auth/validate")
@RequiredArgsConstructor
public class AuthController {
    private final LoginService loginService;

        @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) throws IllegalAccessException {
       return ResponseEntity.ok(loginService.login(loginRequest));
    }


}
