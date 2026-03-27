package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import com.example.ali_08.dto.AuthResponse;
import com.example.ali_08.dto.LoginRequest;
import com.example.ali_08.dto.RegisterRequest;
import com.example.ali_08.dto.UserDTO;
import com.example.ali_08.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest request) {
        UserDTO user = authService.register(request);
        return ResponseEntity.ok(
            ApiResponse.success(user, "Usuario registrado correctamente", HttpStatus.OK.value())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(
                request.getEmail(),
                request.getPassword()
        );
        return ResponseEntity.ok(
            ApiResponse.success(response, "Login exitoso", HttpStatus.OK.value())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(
            ApiResponse.success(null, "Sesión cerrada correctamente", HttpStatus.OK.value())
        );
    }
}
