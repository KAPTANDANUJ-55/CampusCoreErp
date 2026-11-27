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
    private final RegistrationService registrationService;
        @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) throws IllegalAccessException {
       return ResponseEntity.ok(loginService.login(loginRequest));
    }
    @PostMapping("/register{roleType}")
   public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest, @PathVariable String roleType, @RequestHeader(value = "X-User-Role",required = false) String requesterRole) throws IllegalAccessException {
       String normalized = roleType.trim().toLowerCase();


       if (!normalized.startsWith("ROLE_")) normalized = "ROLE_" + normalized;

       Role targetRole;
       try {
           targetRole = Role.valueOf(normalized);
       } catch (IllegalArgumentException e) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + roleType);
       }

       return ResponseEntity.status(HttpStatus.CREATED)
               .body(registrationService.register(registerRequest, targetRole, requesterRole));
   }



}
