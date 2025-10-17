package com.usermanagement.user;

import com.usermanagement.user.utill.Jwtutil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class JwtutilTest {
    private Jwtutil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new Jwtutil();
    }

    @Test
    void generateAndValidateToken_Success() {
        String username = "test@test.com";
        String token = jwtUtil.generateToken(username);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token, username));
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void validateToken_WrongUsername() {
        String username = "test@test.com";
        String wrongUsername = "wrong@test.com";
        String token = jwtUtil.generateToken(username);

        assertFalse(jwtUtil.validateToken(token, wrongUsername));
    }
}
