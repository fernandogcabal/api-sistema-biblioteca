package com.biblioteca.api_sistema_biblioteca.controller;

import com.biblioteca.api_sistema_biblioteca.dto.AuthResponse;
import com.biblioteca.api_sistema_biblioteca.dto.LoginRequest;
import com.biblioteca.api_sistema_biblioteca.dto.UserRegisterRequest;
import com.biblioteca.api_sistema_biblioteca.model.User;
import com.biblioteca.api_sistema_biblioteca.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User registerUser(@RequestBody UserRegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        // 'Principal' es una interfaz de Spring Security que contiene el username extraído del JWT
        String username = principal.getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(Principal principal, @RequestBody UserRegisterRequest request) {
        String username = principal.getName();
        User updatedUser = userService.updateUser(username, request);
        return ResponseEntity.ok(updatedUser);
    }
}
