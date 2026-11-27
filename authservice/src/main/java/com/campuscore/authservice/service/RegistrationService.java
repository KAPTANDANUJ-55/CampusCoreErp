package com.campuscore.authservice.service;

import com.campuscore.authservice.dto.AuthResponse;
import com.campuscore.authservice.dto.RegisterRequest;
import com.campuscore.authservice.dto.RegisterResponse;
import com.campuscore.authservice.entity.AppUser;
import com.campuscore.authservice.entity.Role;
import com.campuscore.authservice.repository.UserRepo;
import com.campuscore.authservice.security.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public AuthResponse register(RegisterRequest registerRequest,Role targetRole,String requesterRole) {
        String cleanUserId = registerRequest.getUserId();
        String cleanEmail = registerRequest.getEmail();
        if(userRepo.existsByUserID(cleanUserId)){
            throw new  ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
        if(userRepo.existsByEmail(cleanEmail)){
            throw new  ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        Role role = parseRole(requesterRole);
        AppUser appUser = new AppUser();
        appUser.setEmail(cleanEmail);
        appUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        appUser.setRole(role);
        appUser.setActive(true);
       AppUser savedUser= userRepo.save(appUser);
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(savedUser))
                .userId(savedUser.getUserId())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .department(savedUser.getDepartment())
                .build();

    }
    private Role parseRole(String roleType) {
        String normalized = roleType.trim().toUpperCase();
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        try {
            return Role.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + roleType);
        }
    }
}
