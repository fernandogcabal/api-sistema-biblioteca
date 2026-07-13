package com.biblioteca.api_sistema_biblioteca.service;

import com.biblioteca.api_sistema_biblioteca.dto.AuthResponse;
import com.biblioteca.api_sistema_biblioteca.dto.LoginRequest;
import com.biblioteca.api_sistema_biblioteca.dto.UserRegisterRequest;
import com.biblioteca.api_sistema_biblioteca.model.User;
import com.biblioteca.api_sistema_biblioteca.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private UserRegisterRequest registerRequest;
    private User testUser;
    private BCryptPasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new BCryptPasswordEncoder();
        
        registerRequest = new UserRegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("securePassword123".toCharArray());

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .passwordHash(encoder.encode("securePassword123"))
                .build();
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterUser_PasswordEncrypted() {
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.registerUser(registerRequest);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        
        assertNotNull(savedUser.getPasswordHash());
        assertTrue(savedUser.getPasswordHash().length() > 0);
        assertNotEquals("securePassword123", savedUser.getPasswordHash());
    }

    @Test
    void testLoginUser_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("password".toCharArray());

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.loginUser(loginRequest),
                "Credenciales inválidas");
    }

    @Test
    void testLoginUser_InvalidPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongPassword".toCharArray());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class, () -> userService.loginUser(loginRequest),
                "Credenciales inválidas");
    }

    @Test
    void testFindByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.findByUsername("nonexistent"),
                "Usuario no encontrado");
    }

    @Test
    void testUpdateUser_Success() {
        UserRegisterRequest updateRequest = new UserRegisterRequest();
        updateRequest.setUsername("newusername");
        updateRequest.setEmail("newemail@example.com");
        updateRequest.setPassword(new char[0]);

        User updatedUser = User.builder()
                .id(1L)
                .username("newusername")
                .email("newemail@example.com")
                .passwordHash(testUser.getPasswordHash())
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser("testuser", updateRequest);

        assertNotNull(result);
        assertEquals("newusername", result.getUsername());
        assertEquals("newemail@example.com", result.getEmail());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_WithNewPassword() {
        UserRegisterRequest updateRequest = new UserRegisterRequest();
        updateRequest.setUsername("testuser");
        updateRequest.setEmail("test@example.com");
        updateRequest.setPassword("newPassword123".toCharArray());

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.updateUser("testuser", updateRequest);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertNotNull(savedUser.getPasswordHash());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_UserNotFound() {
        UserRegisterRequest updateRequest = new UserRegisterRequest();
        updateRequest.setUsername("newusername");
        updateRequest.setEmail("newemail@example.com");
        updateRequest.setPassword(new char[0]);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser("testuser", updateRequest),
                "Usuario no encontrado");
    }
}

