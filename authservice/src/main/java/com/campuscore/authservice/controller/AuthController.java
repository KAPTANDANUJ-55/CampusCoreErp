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
    private final UserRepo userRepo;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(loginService.login(loginRequest));

    }

    @PostMapping("/register/{roleType}")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest,@RequestHeader(value = "X-User-Role", required = false) String requesterRole, @PathVariable String roleType) {
        Role targetRole = parseRole(roleType);
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.register(registerRequest,targetRole,requesterRole));
    }

    private Role parseRole(String roleType) {
        return switch (roleType.trim().toLowerCase()) {
            case "student" -> Role.ROLE_STUDENT;
            case "teacher" -> Role.ROLE_STAFF;
            case "hod" -> Role.ROLE_HOD;
            case "principal" -> Role.ROLE_PRINCIPAL;
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid role type: " + roleType + ". Allowed: student, teacher, hod, principal"
            );
        };
    }

    @GetMapping("/student/{enrollmentNo}")
    private ResponseEntity<StudentInfoDto> getStudentInfo(@PathVariable String enrollmentNo) {
            AppUser student = userRepo.findByUserId(enrollmentNo.trim(),Role.ROLE_STUDENT).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with enrollment no: " + enrollmentNo));

            return ResponseEntity.ok(new  StudentInfoDto(
                    student.getUserId(),
                    student.getDepartment(),
                    student.getFullName(),
                    student.isActive()
            ));
    }
}
