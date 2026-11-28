package com.campuscore.notification;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
class NotificationAuthenticationFilter extends OncePerRequestFilter {

    private final JwtParser parser;

    NotificationAuthenticationFilter(
            @Value("${jwt.public-key:file:../apigateway/src/main/resources/certs/public.pem}") Resource key,
            @Value("${jwt.issuer:campuscore}") String issuer) throws IOException, GeneralSecurityException {
        parser = Jwts.parser()
                .verifyWith(readPublicKey(key))
                .requireIssuer(issuer)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        NotificationUser user;
        try {
            user = authenticate(request.getHeader(HttpHeaders.AUTHORIZATION));
        } catch (JwtException | IllegalArgumentException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                    {"message":"A valid CampusCore sign-in is required."}
                    """);
            return;
        }

        request.setAttribute(NotificationUser.REQUEST_ATTRIBUTE, user);
        chain.doFilter(request, response);
    }

    private NotificationUser authenticate(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Missing bearer token");
        }
        var token = parser.parseSignedClaims(authorization.substring(7));
        if (!Jwts.SIG.RS256.getId().equals(token.getHeader().getAlgorithm())) {
            throw new IllegalArgumentException("Unexpected signature algorithm");
        }
        Claims claims = token.getPayload();
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);
        String department = claims.get("department", String.class);
        if (userId == null || userId.isBlank() || userId.length() > 255 || role == null
                || claims.getExpiration() == null || (department != null && department.length() > 255)) {
            throw new IllegalArgumentException("Invalid identity claims");
        }

        return new NotificationUser(userId, NotificationUser.Role.valueOf(role),
                department == null || department.isBlank() ? null : department.strip());
    }

    private static PublicKey readPublicKey(Resource resource) throws IOException, GeneralSecurityException {
        try (var stream = resource.getInputStream()) {
            String pem = new String(stream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            var spec = new X509EncodedKeySpec(Base64.getDecoder().decode(pem));
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        }
    }
}
