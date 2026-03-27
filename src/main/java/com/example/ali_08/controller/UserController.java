package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<String>> ping() {
        return ResponseEntity.ok(
            ApiResponse.success("Backend activo ALV 🚀", "Ping exitoso", HttpStatus.OK.value())
        );
    }
}