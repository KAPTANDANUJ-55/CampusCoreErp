package com.campuscore.authservice.service;

import com.campuscore.authservice.dto.AuthResponse;
import com.campuscore.authservice.dto.RegisterRequest;
import com.campuscore.authservice.dto.RegisterResponse;
import com.campuscore.authservice.entity.AppUser;
import com.campuscore.authservice.entity.Role;
import com.campuscore.authservice.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepo userRepo;
    private PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest registerRequest,Role targetRole, String requesteerRole){

        validatePermission(targetRole, requesteerRole);
       if(userRepo.findByUserId(registerRequest.getUserId()).isPresent()){
           throw new ResponseStatusException(HttpStatus.CONFLICT,"Seems Like Already Registered");
       }
       if(userRepo.existsByEmail(registerRequest.getEmail())){
           throw new ResponseStatusException(HttpStatus.CONFLICT,"Seems Like Email Already Exists");
       }

       AppUser appUser = userRepo.save(AppUser.builder().userId(registerRequest.getUserId()).fullName(registerRequest.getFullName()).email(registerRequest.getEmail()).isActive(true).department(registerRequest.getDepartment()).build());

       return AuthResponse.builder().fullName(appUser.getFullName()).build();
    }
    private void validatePermission(Role targetRole, String requesterRole) {
        switch (targetRole) {
            case ROLE_PRINCIPAL -> {
                if (userRepo.existsByRole(Role.ROLE_PRINCIPAL)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Principal account already exists!");
                }
            }
            case ROLE_HOD -> {
                if (!"ROLE_PRINCIPAL".equals(requesterRole)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only Principal can register an HOD!");
                }
            }
            case ROLE_STAFF -> {
                if (!"ROLE_PRINCIPAL".equals(requesterRole) && !"ROLE_HOD".equals(requesterRole)) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only Principal or HOD can register a Teacher!");
                }
            }
            case ROLE_STUDENT -> {
                // Public signup ya staff onboarding allowed
            }
        }
    }

    public String generateRoleBasedId(Role role, String department){
        long nextNumber = userRepo.countByRole(role) +1;
        String dept = (department != null && !department.isBlank()) ? department.trim().toUpperCase() : "GEN";

        return switch (role) {
            case ROLE_STUDENT -> String.format("STU-%s-%04d", dept, nextNumber);   // e.g., STU-CE-0001
            case ROLE_STAFF -> String.format("TCH-%s-%03d", dept, nextNumber);   // e.g., TCH-CE-001
            case ROLE_HOD -> String.format("HOD-%s-%02d", dept, nextNumber);       // e.g., HOD-CE-01
            case ROLE_PRINCIPAL -> String.format("PRI-%02d", nextNumber);          // e.g., PRI-01
        };
    }
}
