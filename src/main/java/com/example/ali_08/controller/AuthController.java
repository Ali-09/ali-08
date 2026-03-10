package com.example.ali_08.controller;

import com.example.ali_08.dto.AuthResponse;
import com.example.ali_08.dto.LoginRequest;
import com.example.ali_08.dto.RegisterRequest;
import com.example.ali_08.service.AuthService;

import jakarta.validation.Valid;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
         return ResponseEntity.ok(
            Map.of(
                "message", "Usuario registrado correctamente",
                "status", 200
            )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
