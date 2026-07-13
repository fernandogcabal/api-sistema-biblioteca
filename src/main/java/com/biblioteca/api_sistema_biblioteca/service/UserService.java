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

    private final JwtService jwtService;
    private final UserRepository userRepository;
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
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

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

        // Generamos su token y lo retornamos en el DTO
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     * Requerido por el endpoint GET /api/users/me
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el username: " + username));
    }

    /**
     * Actualiza la información del perfil del usuario logueado.
     * Requerido por el endpoint PUT /api/users/me
     */
    public User updateUser(String currentUsername, UserRegisterRequest request) {
        // 1. Buscar el usuario existente en la base de datos
        User existingUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Actualizar los campos básicos
        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());

        // 3. Si se envía una nueva contraseña, la encriptamos de forma segura
        if (request.getPassword() != null && request.getPassword().length > 0 && request.getPassword()[0] != '0') {
            byte[] passwordBytes = StandardCharsets.UTF_8.encode(CharBuffer.wrap(request.getPassword())).array();
            String encryptedPassword = passwordEncoder.encode(new String(passwordBytes, StandardCharsets.UTF_8));
            existingUser.setPasswordHash(encryptedPassword);

            // Limpieza higiénica de memoria de contraseñas
            Arrays.fill(passwordBytes, (byte) 0);
            Arrays.fill(request.getPassword(), '0');
        }

        // 4. Guardar los cambios en PostgreSQL
        return userRepository.save(existingUser);
    }
}