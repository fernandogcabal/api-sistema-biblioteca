package com.biblioteca.api_sistema_biblioteca.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private String testUsername;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        testUsername = "testuser";
    }

    @Test
    void testGenerateToken_Success() {
        String token = jwtService.generateToken(testUsername);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.length() > 0);
    }

    @Test
    void testGenerateToken_ConsistentFormat() {
        String token1 = jwtService.generateToken(testUsername);

        assertNotNull(token1);
        assertFalse(token1.isEmpty());
        assertTrue(token1.length() > 0);
    }

    @Test
    void testExtractUsername_Success() {
        String token = jwtService.generateToken(testUsername);

        String extractedUsername = jwtService.extractUsername(token);

        assertNotNull(extractedUsername);
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void testExtractUsername_DifferentUsers() {
        String user1 = "user1";
        String user2 = "user2";
        
        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);

        assertEquals(user1, jwtService.extractUsername(token1));
        assertEquals(user2, jwtService.extractUsername(token2));
        assertNotEquals(jwtService.extractUsername(token1), jwtService.extractUsername(token2));
    }

    @Test
    void testIsTokenValid_Success() {
        String token = jwtService.generateToken(testUsername);

        boolean isValid = jwtService.isTokenValid(token, testUsername);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_InvalidUsername() {
        String token = jwtService.generateToken(testUsername);

        boolean isValid = jwtService.isTokenValid(token, "differentuser");

        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_WrongUsername() {
        String token = jwtService.generateToken("user1");

        boolean isValid = jwtService.isTokenValid(token, "user2");

        assertFalse(isValid);
    }

    @Test
    void testIsTokenValid_SameUsernameMultipleTimes() {
        String token = jwtService.generateToken(testUsername);

        assertTrue(jwtService.isTokenValid(token, testUsername));
        assertTrue(jwtService.isTokenValid(token, testUsername));
        assertTrue(jwtService.isTokenValid(token, testUsername));
    }

    @Test
    void testGenerateToken_ContainsCorrectUsername() {
        String token = jwtService.generateToken(testUsername);

        String username = jwtService.extractUsername(token);

        assertEquals(testUsername, username);
    }

    @Test
    void testTokenExpiration_NotExpiredImmediately() {
        String token = jwtService.generateToken(testUsername);

        boolean isValid = jwtService.isTokenValid(token, testUsername);

        assertTrue(isValid);
    }

    @Test
    void testExtractUsername_ValidatesCorrectly() {
        String[] users = {"alice", "bob", "charlie"};

        for (String user : users) {
            String token = jwtService.generateToken(user);
            assertEquals(user, jwtService.extractUsername(token));
        }
    }

    @Test
    void testMultipleTokens_Independent() {
        String token1 = jwtService.generateToken("user1");
        String token2 = jwtService.generateToken("user2");

        assertTrue(jwtService.isTokenValid(token1, "user1"));
        assertTrue(jwtService.isTokenValid(token2, "user2"));
        assertFalse(jwtService.isTokenValid(token1, "user2"));
        assertFalse(jwtService.isTokenValid(token2, "user1"));
    }

    @Test
    void testGenerateToken_TokenStructure() {
        String token = jwtService.generateToken(testUsername);

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    void testExtractClaim_WithUsernameFunction() {
        String token = jwtService.generateToken(testUsername);

        String extracted = jwtService.extractUsername(token);

        assertEquals(testUsername, extracted);
    }

    @Test
    void testIsTokenValid_CaseSensitive() {
        String token = jwtService.generateToken("TestUser");

        boolean validWithSameCasing = jwtService.isTokenValid(token, "TestUser");
        boolean validWithDifferentCasing = jwtService.isTokenValid(token, "testuser");

        assertTrue(validWithSameCasing);
        assertFalse(validWithDifferentCasing);
    }

    @Test
    void testGenerateAndValidate_CompleteFlow() {
        String user = "flowuser";

        String token = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(token, user));

        String extracted = jwtService.extractUsername(token);
        assertEquals(user, extracted);

        assertTrue(jwtService.isTokenValid(token, extracted));
    }
}
