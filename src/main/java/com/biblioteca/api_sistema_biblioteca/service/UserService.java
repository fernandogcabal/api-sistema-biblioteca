package com.biblioteca.api_sistema_biblioteca.service;

import com.biblioteca.api_sistema_biblioteca.dto.AuthResponse;
import com.biblioteca.api_sistema_biblioteca.dto.LoginRequest;
import com.biblioteca.api_sistema_biblioteca.dto.UserRegisterRequest;
import com.biblioteca.api_sistema_biblioteca.model.User;
import com.biblioteca.api_sistema_biblioteca.repository.UserRepository;
import com.biblioteca.api_sistema_biblioteca.util.ValidationUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class UserService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public User registerUser(UserRegisterRequest request) {
        ValidationUtil.validateUsername(request.getUsername());
        ValidationUtil.validateEmail(request.getEmail());
        ValidationUtil.validatePassword(request.getPassword());

        byte[] passwordBytes = StandardCharsets.UTF_8.encode(CharBuffer.wrap(request.getPassword())).array();

        String realPasswordHash = passwordEncoder.encode(new String(passwordBytes, StandardCharsets.UTF_8));

        User userEntity = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(realPasswordHash)
                .build();

        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(request.getPassword(), '0');

        return userRepository.save(userEntity);
    }

    public AuthResponse loginUser(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        byte[] passwordBytes = StandardCharsets.UTF_8.encode(CharBuffer.wrap(request.getPassword())).array();
        String rawPassword = new String(passwordBytes, StandardCharsets.UTF_8);

        boolean matches = passwordEncoder.matches(rawPassword, user.getPasswordHash());

        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(request.getPassword(), '0');

        if (!matches) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     * Requerido por el endpoint GET /api/users/me
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Actualiza la información del perfil del usuario logueado.
     * Requerido por el endpoint PUT /api/users/me
     */
    public User updateUser(String currentUsername, UserRegisterRequest request) {
        // 1. Buscar el usuario existente en la base de datos
        User existingUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ValidationUtil.validateUsername(request.getUsername());
        ValidationUtil.validateEmail(request.getEmail());
        
        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());

        if (request.getPassword() != null && request.getPassword().length > 0 && request.getPassword()[0] != '0') {
            ValidationUtil.validatePassword(request.getPassword());
            
            byte[] passwordBytes = StandardCharsets.UTF_8.encode(CharBuffer.wrap(request.getPassword())).array();
            String encryptedPassword = passwordEncoder.encode(new String(passwordBytes, StandardCharsets.UTF_8));
            existingUser.setPasswordHash(encryptedPassword);

            Arrays.fill(passwordBytes, (byte) 0);
            Arrays.fill(request.getPassword(), '0');
        }

        return userRepository.save(existingUser);
    }
}