package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import com.example.ali_08.dto.ProfileUpdateRequest;
import com.example.ali_08.dto.UserDTO;
import com.example.ali_08.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final ProfileService profileService;

    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<String>> ping() {
        return ResponseEntity.ok(
            ApiResponse.success("Backend activo ALV 🚀", "Ping exitoso", HttpStatus.OK.value())
        );
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getProfile() {
        UserDTO user = profileService.getProfile();
        return ResponseEntity.ok(
            ApiResponse.success(user, "Perfil obtenido correctamente", HttpStatus.OK.value())
        );
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        UserDTO updatedUser = profileService.updateProfile(request);
        return ResponseEntity.ok(
            ApiResponse.success(updatedUser, "Perfil actualizado correctamente", HttpStatus.OK.value())
        );
    }
}