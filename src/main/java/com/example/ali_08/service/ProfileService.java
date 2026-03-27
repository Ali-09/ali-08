package com.example.ali_08.service;

import com.example.ali_08.dto.ProfileUpdateRequest;
import com.example.ali_08.dto.UserDTO;
import com.example.ali_08.exception.UserAlreadyExistsException;
import com.example.ali_08.model.User;
import com.example.ali_08.model.UserProfile;
import com.example.ali_08.repository.UserProfileRepository;
import com.example.ali_08.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    public UserDTO getProfile() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> UserProfile.builder().user(user).build());

        return UserDTO.builder()
                .name(profile.getFirstName())
                .email(user.getEmail())
                .salary(profile.getSalary() != null ? profile.getSalary().doubleValue() : 0.0)
                .currencyId(1L) // Default currency_id
                .build();
    }

    @Transactional
    public UserDTO updateProfile(ProfileUpdateRequest request) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar si el nuevo email ya está en uso por otro usuario
        if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("El email ya está en uso");
        }

        user.setEmail(request.getEmail());
        userRepository.save(user);

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> UserProfile.builder().user(user).build());

        profile.setFirstName(request.getName());
        if (request.getSalary() != null) {
            profile.setSalary(BigDecimal.valueOf(request.getSalary()));
        }
        userProfileRepository.save(profile);

        return UserDTO.builder()
                .name(profile.getFirstName())
                .email(user.getEmail())
                .salary(profile.getSalary() != null ? profile.getSalary().doubleValue() : 0.0)
                .currencyId(request.getCurrencyId() != null ? request.getCurrencyId() : 1L)
                .build();
    }
}
