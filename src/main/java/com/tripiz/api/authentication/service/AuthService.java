package com.tripiz.api.authentication.service;

import com.tripiz.api.authentication.dto.LoginRequest;
import com.tripiz.api.authentication.dto.RegisterRequest;
import com.tripiz.api.authentication.dto.RegisterResponse;
import com.tripiz.api.authentication.dto.TokenResponse;
import com.tripiz.api.domain.User;
import com.tripiz.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

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

    public TokenResponse login(LoginRequest request) {
        // Construction du body de la requête (application/x-www-form-urlencoded)
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "tripiz-client");
        body.add("username", request.getUsername());
        body.add("password", request.getPassword());
        body.add("grant_type", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        // Appel à Keycloak
        RestTemplate restTemplate = new RestTemplate();
        String keycloakUrl = "https://keycloak-production-53a7.up.railway.app/realms/tripiz/protocol/openid-connect/token";
        // ou récupérer l'URL depuis application.properties (ex: keycloak.auth-server-url)

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(keycloakUrl, entity, Map.class);
            Map<String, Object> responseBody = response.getBody();

            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setAccessToken((String) responseBody.get("access_token"));
            tokenResponse.setRefreshToken((String) responseBody.get("refresh_token"));
            tokenResponse.setTokenType((String) responseBody.get("token_type"));
            tokenResponse.setExpiresIn(((Number) responseBody.get("expires_in")).longValue());

            return tokenResponse;
        } catch (HttpClientErrorException e) {
            // Gérer les erreurs (mauvais login, etc.)
            throw new RuntimeException("Authentication failed: " + e.getResponseBodyAsString());
        }
    }
}