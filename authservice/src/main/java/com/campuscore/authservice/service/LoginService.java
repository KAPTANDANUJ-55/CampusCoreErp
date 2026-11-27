package com.campuscore.authservice.service;
import com.campuscore.authservice.dto.AuthResponse;
import com.campuscore.authservice.dto.LoginRequest;
import com.campuscore.authservice.entity.UserToken;
import com.campuscore.authservice.entity.AppUser;
import com.campuscore.authservice.repository.TokenRepo;
import com.campuscore.authservice.repository.UserRepo;
import com.campuscore.authservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoginService {
   private final UserRepo userRepo;
   private final JwtUtil jwtUtil;
   private final PasswordEncoder passwordEncoder;


   public AuthResponse login(LoginRequest loginRequest) throws IllegalAccessException {
       String id = loginRequest.getUserID();
       AppUser user = userRepo.findByUserID(id).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"User not found"));
       if(user.getPassword()==null){
           throw new BadCredentialsException("This account uses Google Sign-In. Please login with Google.");
       }

       if(!user.isActive()){
           throw new IllegalAccessException("Account is not active please contact college admin");
       }

        if( !user.getPassword().equals(loginRequest.getPassword())){
            throw new BadCredentialsException("Invalid credentials");
       }

        String token = jwtUtil.generateToken(user);

       return AuthResponse.builder()
               .token(token)
               .userId(user.getUserId())
               .fullName(user.getFullName())
               .role(user.getRole())
               .department(user.getDepartment())
               .build();

   }
}
