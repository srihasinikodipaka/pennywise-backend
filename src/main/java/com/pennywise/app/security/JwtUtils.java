package com.pennywise.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private static final String DEV_FALLBACK_KEY = "dev_only_insecure_jwt_secret_key_minimum_32_bytes_long_pennywise_development!";

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @PostConstruct
    public void validateSecurityConfiguration() {
        boolean isProduction = activeProfile != null && 
                (activeProfile.equalsIgnoreCase("prod") || activeProfile.equalsIgnoreCase("production"));

        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            if (isProduction) {
                throw new IllegalStateException("FATAL: JWT_SECRET environment variable is missing in production environment. A strong 256-bit key must be supplied.");
            }
            logger.warn("JWT secret is not configured; using local development key.");
            jwtSecret = DEV_FALLBACK_KEY;
        } else if (isProduction && jwtSecret.contains("dev_only_insecure")) {
            throw new IllegalStateException("FATAL: Default development JWT secret detected in production profile! A unique, high-entropy JWT_SECRET must be supplied via environment variables.");
        } else if (isProduction && jwtSecret.length() < 32) {
            throw new IllegalStateException("FATAL: JWT_SECRET must be at least 256 bits (32 characters or 64 hex characters) in production.");
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
            if (keyBytes.length < 32) {
                keyBytes = jwtSecret.getBytes();
            }
        } catch (Exception e) {
            keyBytes = jwtSecret.getBytes();
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // invalid token
            return false;
        }
    }
}
