package com.campuscore.authservice.service;
import com.campuscore.authservice.dto.AuthResponse;
import com.campuscore.authservice.dto.LoginRequest;
import com.campuscore.authservice.entity.UserToken;
import com.campuscore.authservice.entity.AppUser;
import com.campuscore.authservice.repository.TokenRepo;
import com.campuscore.authservice.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {

}
