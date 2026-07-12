package com.biblioteca.api_sistema_biblioteca.service;

import com.biblioteca.api_sistema_biblioteca.dto.AuthResponse;
import com.biblioteca.api_sistema_biblioteca.dto.LoginRequest;
import com.biblioteca.api_sistema_biblioteca.dto.UserRegisterRequest;
import com.biblioteca.api_sistema_biblioteca.model.User;
import com.biblioteca.api_sistema_biblioteca.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class UserService {
    // ... Inyección en el constructor ...
    private final JwtService jwtService;
    private final UserRepository userRepository;
    // Instanciamos el codificador oficial de BCrypt
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public User registerUser(UserRegisterRequest request) {
// 1. Convertimos el char[] de forma segura a bytes para que BCrypt lo procese
        byte[] passwordBytes = StandardCharsets.UTF_8.encode(CharBuffer.wrap(request.getPassword())).array();

        // 2. Encriptamos con BCrypt (Genera un hash irreversible de 60 caracteres)
        String realPasswordHash = passwordEncoder.encode(new String(passwordBytes, StandardCharsets.UTF_8));

        // 3. Construimos la entidad con el Hash real e irreversible
        User userEntity = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(realPasswordHash)
                .build();

        // 4. ¡SEGURIDAD ABSOLUTA!: Destruimos tanto los bytes temporales como el char[] original
        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(request.getPassword(), '0');

        // 5. Guardamos en PostgreSQL
        return userRepository.save(userEntity);
    }

    public AuthResponse loginUser(LoginRequest request) {
        // 1. Buscar al usuario por username
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas")); // Por seguridad, no decimos si lo que falló fue el usuario o la contraseña

        // 2. Convertir temporalmente el char[] a bytes para la comparación de BCrypt
        byte[] passwordBytes = StandardCharsets.UTF_8.encode(CharBuffer.wrap(request.getPassword())).array();
        String rawPassword = new String(passwordBytes, StandardCharsets.UTF_8);

        // 3. BCrypt compara el texto plano contra el Hash guardado
        boolean matches = passwordEncoder.matches(rawPassword, user.getPasswordHash());

        // 4. ¡SEGURIDAD ABSOLUTA!: Limpiamos los arreglos inmediatamente
        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(request.getPassword(), '0');

        // 5. Si no coincide, lanzamos excepción
        if (!matches) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // En lugar de retornar 'user', generamos su token y lo retornamos en el DTO
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

}
