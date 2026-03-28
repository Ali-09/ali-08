package com.example.ali_08.controller;

import com.example.ali_08.dto.ApiResponse;
import com.example.ali_08.dto.ProfileRequest;
import com.example.ali_08.dto.UserDTO;
import com.example.ali_08.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserDTO>> getProfile() {
        UserDTO user = profileService.getProfile();
        return ResponseEntity.ok(
            ApiResponse.success(user, "Perfil obtenido correctamente", HttpStatus.OK.value())
        );
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<UserDTO>> updateProfile(@Valid @RequestBody ProfileRequest request) {
        UserDTO updatedUser = profileService.updateProfile(request);
        return ResponseEntity.ok(
            ApiResponse.success(updatedUser, "Perfil actualizado correctamente", HttpStatus.OK.value())
        );
    }
}
