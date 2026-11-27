package com.campuscore.apigateway.filter;

import com.campuscore.apigateway.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Value("${internal.secret}")
    private String internalSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String path = req.getRequestURI();

        // Checkpoint 1: Login/Register ko bina token check ke jaane do
        if (path.startsWith("/api/auth")) {
            chain.doFilter(req, res);
            return;
        }

        // Checkpoint 2: Bearer token nikaalo aur verify karo
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            sendError(res, 401, "MISSING_TOKEN", "Authorization Bearer token missing.");
            return;
        }

        Claims claims;
        try {
            claims = jwtUtil.extractClaims(header.substring(7));
        } catch (SignatureException e) {
            sendError(res, 401, "SIGNATURE_TAMPERED", "Nice try editing the JWT payload. RS256 signature says no.");
            return;
        } catch (Exception e) {
            sendError(res, 401, "INVALID_TOKEN", "Token expired or invalid.");
            return;
        }

        String userId = claims.getSubject();
        String role = claims.get("role", String.class);

        // Checkpoint 3: RBAC Check
        if (!hasAccess(path, req.getMethod(), role)) {
            sendError(res, 403, "ACCESS_DENIED", "Wrong clearance level. Role " + role + " cannot access " + path);
            return;
        }

        // Checkpoint 4: Verified headers attach karke downstream service ko bhejo
        HttpServletRequest wrapped = new HttpServletRequestWrapper(req) {
            @Override
            public String getHeader(String name) {
                if ("X-User-Id".equalsIgnoreCase(name)) return userId;
                if ("X-User-Role".equalsIgnoreCase(name)) return role;
                if ("X-Internal-Secret".equalsIgnoreCase(name)) return internalSecret;
                return super.getHeader(name);
            }
        };

        chain.doFilter(wrapped, res);
    }

    private boolean hasAccess(String path, String method, String role) {
        if ("ROLE_PRINCIPAL".equals(role)) return true;

        // Admin routes -> Sirf Teacher ya HOD
        if (path.startsWith("/api/v1/admin")) {
            return List.of("ROLE_TEACHER", "ROLE_HOD").contains(role);
        }

        // Marks ya Attendance create/update/delete -> Sirf Teacher ya HOD
        if ((path.startsWith("/api/v1/marks") || path.startsWith("/api/v1/attendance"))
                && !method.equalsIgnoreCase("GET")) {
            return List.of("ROLE_TEACHER", "ROLE_HOD").contains(role);
        }

        // Baaki GET requests -> Sab logged-in users allowed
        return method.equalsIgnoreCase("GET");
    }

    private void sendError(HttpServletResponse res, int status, String code, String msg) throws IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.getWriter().write(String.format("{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}", status, code, msg));
    }
}