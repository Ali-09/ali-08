package com.example.ali_08.service;

import com.example.ali_08.dto.ProfileRequest;
import com.example.ali_08.dto.UserDTO;
import com.example.ali_08.exception.UserAlreadyExistsException;
import com.example.ali_08.model.IncomeFrequency;
import com.example.ali_08.model.User;
import com.example.ali_08.model.UserProfile;
import com.example.ali_08.repository.IncomeFrequencyRepository;
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
    private final IncomeFrequencyRepository incomeFrequencyRepository;

    public UserDTO getProfile() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseGet(() -> UserProfile.builder().user(user).build());

        IncomeFrequency frequency = profile.getIncomeFrequency();

        return UserDTO.builder()
                .name(profile.getFirstName())
                .email(user.getEmail())
                .salary(profile.getSalary() != null ? profile.getSalary().doubleValue() : 0.0)
                .currencyId(1L) // Default currency_id
                .incomeFrequencyId(frequency != null ? frequency.getId() : null)
                .incomeFrequencyName(frequency != null ? frequency.getName() : null)
                .build();
    }

    @Transactional
    public UserDTO updateProfile(ProfileRequest request) {
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

        if (request.getIncomeFrequencyId() != null) {
            IncomeFrequency frequency = incomeFrequencyRepository.findById(request.getIncomeFrequencyId())
                    .orElseThrow(() -> new RuntimeException("Periodo no encontrado"));
            profile.setIncomeFrequency(frequency);
        }
        userProfileRepository.save(profile);

        IncomeFrequency frequency = profile.getIncomeFrequency();

        return UserDTO.builder()
                .name(profile.getFirstName())
                .email(user.getEmail())
                .salary(profile.getSalary() != null ? profile.getSalary().doubleValue() : 0.0)
                .currencyId(request.getCurrencyId() != null ? request.getCurrencyId() : 1L)
                .incomeFrequencyId(frequency != null ? frequency.getId() : null)
                .incomeFrequencyName(frequency != null ? frequency.getName() : null)
                .build();
    }
}
