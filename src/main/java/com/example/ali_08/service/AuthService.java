package com.example.ali_08.service;

import com.example.ali_08.dto.AuthResponse;
import com.example.ali_08.dto.RegisterRequest;
import com.example.ali_08.dto.UserDTO;
import com.example.ali_08.exception.InvalidCredentialsException;
import com.example.ali_08.exception.UserAlreadyExistsException;
import com.example.ali_08.model.User;
import com.example.ali_08.model.UserProfile;
import com.example.ali_08.repository.CurrencyRepository;
import com.example.ali_08.repository.UserProfileRepository;
import com.example.ali_08.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CurrencyRepository currencyRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public UserDTO register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("El usuario ya existe");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        UserProfile profile = UserProfile.builder()
                .user(user)
                .firstName(request.getName())
                .salary(BigDecimal.ZERO)
                .build();

        userProfileRepository.save(profile);

        return UserDTO.builder()
                .name(profile.getFirstName())
                .email(user.getEmail())
                .salary(profile.getSalary().doubleValue())
                .currencyId(1L) // Default currency_id
                .build();
    }

    public AuthResponse login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Email o contraseña incorrectos"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Email o contraseña incorrectos");
        }

        String token = jwtService.generateToken(user.getEmail());
        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);

        UserDTO userDTO = UserDTO.builder()
                .email(user.getEmail())
                .name(profile != null ? profile.getFirstName() : null)
                .salary(profile != null && profile.getSalary() != null ? profile.getSalary().doubleValue() : null)
                .currencyId(1L) // Default currency_id as requested
                .build();

        return new AuthResponse(token, userDTO);
    }
}
