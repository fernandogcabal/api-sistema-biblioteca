package com.biblioteca.api_sistema_biblioteca.controller;

import com.biblioteca.api_sistema_biblioteca.dto.AuthResponse;
import com.biblioteca.api_sistema_biblioteca.dto.LoginRequest;
import com.biblioteca.api_sistema_biblioteca.dto.UserRegisterRequest;
import com.biblioteca.api_sistema_biblioteca.model.User;
import com.biblioteca.api_sistema_biblioteca.service.UserService;
import org.springframework.web.bind.annotation.*;

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
}
