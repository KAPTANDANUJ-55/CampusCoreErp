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

   private final UserRepo userRepo;
   private final PasswordEncoder passwordEncoder;
   private final TokenRepo tokenRepo;

   public AuthResponse login(LoginRequest loginRequest) {
       AppUser user = userRepo.findByUserId(loginRequest.getUserID()).orElseThrow(()->
               new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid username or password")
               );

       if(!user.isActive() || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
           throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT,"Please enter valid credentials the tea taste is different!!");

       }
String token = UUID.randomUUID().toString();
       tokenRepo.save(UserToken.builder().userId(loginRequest.getUserID()).expiryDate(LocalDateTime.now()).token(token).build());


       return AuthResponse.builder().role(user.getRole()).department(user.getDepartment()).fullName(user.getFullName()).token(token).userId(user.getUserId()).build();
   }

}
