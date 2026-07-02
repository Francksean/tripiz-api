package com.tripiz.api.authentication.controller;

import com.tripiz.api.authentication.dto.LoginRequest;
import com.tripiz.api.authentication.dto.RegisterRequest;
import com.tripiz.api.authentication.dto.RegisterResponse;
import com.tripiz.api.authentication.dto.TokenResponse;
import com.tripiz.api.authentication.service.AuthService;
import com.tripiz.api.domain.User;
import com.tripiz.api.model.UserDTO;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.service.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.registerClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register/driver")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<RegisterResponse> registerDriver(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.registerDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();  // ← OK avec le bon import
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(userMapper.toUserDTO(user));
    }
}