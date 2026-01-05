package utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtTokenProvider {
    private final SecretKey jwtSecret;
    private final int jwtExpirationInMs;

    public JwtTokenProvider(String secret, int expirationInMs) {
        this.jwtSecret = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationInMs = expirationInMs;
    }

    public JwtTokenProvider() {
        // Using a default secret key - in production, use environment variables
        this("mySecretKeyForTokenGenerationThatShouldBeAtLeast256BitsLong", 86400000); // 24 hours
    }

    public String generateToken(Long userId) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(jwtSecret)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(jwtSecret)
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Invalid JWT token
            return false;
        }
    }
}