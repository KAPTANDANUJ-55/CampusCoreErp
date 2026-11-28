package com.campuscore.authservice.security;

import com.campuscore.authservice.entity.AppUser;
import com.campuscore.authservice.entity.Role;
import com.campuscore.authservice.repository.UserRepo;
import com.campuscore.authservice.repository.UserRepo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepo userRepository;
    private final JwtUtil jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String fullName = oAuth2User.getAttribute("name");

        AppUser user = userRepository.findByEmail(email).orElseGet(() -> {
            AppUser newUser = new AppUser();
            newUser.setEmail(email);
            newUser.setFullName(fullName != null ? fullName : email.split("@")[0]);
            newUser.setUserId("G-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            newUser.setPassword(""); // OAuth user ke liye password blank
            newUser.setDepartment("GENERAL");
            newUser.setRole(Role.ROLE_STUDENT); // Default role
            newUser.setActive(true);
            return userRepository.save(newUser);
        });

        String token = jwtService.generateToken(user);

        String targetUrl = UriComponentsBuilder
                .fromUriString("http://localhost:5173/oauth2/redirect")
                .queryParam("token", token)
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}