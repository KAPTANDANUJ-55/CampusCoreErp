    package com.campuscore.apigateway.security;

    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.Jwts;
    import jakarta.annotation.PostConstruct;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.core.io.Resource;
    import org.springframework.stereotype.Component;

    import java.nio.charset.StandardCharsets;
    import java.security.KeyFactory;
    import java.security.PublicKey;
    import java.security.spec.X509EncodedKeySpec;
    import java.util.Base64;

    @Component
    public class JwtUtil {

        @Value("${jwt.public-key}")
        private Resource publicKeyResource;

        @Value("${jwt.issuer}")
        private String issuer;


        private PublicKey publicKey;

        @PostConstruct
        public void init() throws Exception {
            String key = new String(publicKeyResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");
            byte[] decoded = Base64.getDecoder().decode(key);
            this.publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        }

        public Claims extractClaims(String token) {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
    }