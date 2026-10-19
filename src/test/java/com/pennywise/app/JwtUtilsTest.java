package com.pennywise.app;

import com.pennywise.app.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;
    // 256-bit (32 bytes) test key for HMAC-SHA256
    private static final String VALID_TEST_SECRET = "unit_test_secure_secret_key_with_at_least_32_characters_length_ok!";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", VALID_TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtUtils, "activeProfile", "dev");
    }

    @Test
    @DisplayName("Should generate token, extract username, and validate token successfully")
    void testTokenGenerationAndValidation() {
        String username = "testuser";
        String token = jwtUtils.generateToken(username);

        assertNotNull(token);
        assertFalse(token.isBlank());

        assertTrue(jwtUtils.validateToken(token));
        assertEquals(username, jwtUtils.getUsernameFromJwt(token));
    }

    @Test
    @DisplayName("Should reject invalid or tampered JWT token")
    void testInvalidTokenRejection() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.invalidPayload.tamperedSignature";
        assertFalse(jwtUtils.validateToken(invalidToken));
    }

    @Test
    @DisplayName("Should reject null or empty token")
    void testEmptyTokenRejection() {
        assertFalse(jwtUtils.validateToken(null));
        assertFalse(jwtUtils.validateToken(""));
        assertFalse(jwtUtils.validateToken("   "));
    }

    @Test
    @DisplayName("Should enforce non-development secret when in production profile")
    void testProductionValidationEnforcesStrongSecret() {
        JwtUtils prodJwt = new JwtUtils();
        ReflectionTestUtils.setField(prodJwt, "activeProfile", "prod");
        ReflectionTestUtils.setField(prodJwt, "jwtSecret", "dev_only_insecure_default_key");

        // Should throw IllegalStateException because dev key was detected in prod profile
        assertThrows(IllegalStateException.class, prodJwt::validateSecurityConfiguration);
    }
}
