package com.campuscore.authservice.security;

import com.campuscore.authservice.entity.AppUser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

 @Value("${jwt.private-key}")
private Resource privateKeyResource;
@Value("${jwt.issuer}")
private String issuer;
@Value("${jwt.expiration}")
public long expirationMs;

private PrivateKey privateKey;

@PostConstruct
public void init() throws Exception{
    String key = new String(privateKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8).replace("-----BEGIN PRIVATE KEY-----","").replace("-----END PRIVATE KEY-----", "").replaceAll("\\s+", "");
    byte[] decoded = Base64.getDecoder().decode(key.getBytes(StandardCharsets.UTF_8));
    this.privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));

}
    public String generateToken(AppUser user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUserId())
                .issuer(issuer)
                .claim("role", user.getRole().name())
                .claim("department", user.getDepartment())
                .claim("note","Nice try decoding this, RS256 signature won't match anyway.")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }



}
