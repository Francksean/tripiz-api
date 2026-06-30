package com.tripiz.api.authentication.service;

import com.tripiz.api.authentication.dto.RegisterRequest;
import com.tripiz.api.authentication.dto.RegisterResponse;
import com.tripiz.api.domain.User;
import com.tripiz.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse registerClient(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String password = request.getPassword();
        if (!password.matches("^(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            throw new IllegalArgumentException("Password must be at least 8 characters and contain at least one digit");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(password)) // encodage BCrypt
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone() != null ? Integer.valueOf(request.getPhone()) : null)
                .createdAt(LocalDateTime.now())
                .role("client")
                .status("ONLINE")
                .build();

        User saved = userRepository.save(user);
        return new RegisterResponse(saved.getUserId(), saved.getEmail(), saved.getRole());
    }

    @Transactional
    public RegisterResponse registerDriver(RegisterRequest request) {
        // Similaire, mais avec le rôle "driver" (accessible uniquement via un endpoint admin)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String password = request.getPassword();
        if (!password.matches("^(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            throw new IllegalArgumentException("Invalid password format");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(password))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone() != null ? Integer.valueOf(request.getPhone()) : null)
                .createdAt(LocalDateTime.now())
                .role("driver")
                .status("ONLINE")
                .build();

        User saved = userRepository.save(user);
        return new RegisterResponse(saved.getUserId(), saved.getEmail(), saved.getRole());
    }
}