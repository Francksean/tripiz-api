package com.tripiz.api.authentication.service;

import com.tripiz.api.authentication.dto.LoginRequest;
import com.tripiz.api.authentication.dto.RegisterRequest;
import com.tripiz.api.authentication.dto.RegisterResponse;
import com.tripiz.api.authentication.dto.TokenResponse;
import com.tripiz.api.domain.User;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    private final KeycloakUserService keycloakUserService;
    private final WalletService walletService; // 👈 Injecté

    @Value("${keycloak.admin-client-secret}")
    private String clientSecret;

    @Transactional
    public RegisterResponse registerClient(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String keycloakId = keycloakUserService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName());

        User user = User.builder()
                .keycloakId(keycloakId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role("client")
                .status("ONLINE")
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        walletService.createWalletForUser(saved.getUserId());

        return new RegisterResponse(saved.getUserId(), saved.getEmail(), saved.getRole());
    }

    @Transactional
    public RegisterResponse registerDriver(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String keycloakId = keycloakUserService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName());

        User user = User.builder()
                .keycloakId(keycloakId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .role("driver")
                .status("ONLINE")
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        walletService.createWalletForUser(saved.getUserId());

        return new RegisterResponse(saved.getUserId(), saved.getEmail(), saved.getRole());
    }

    public TokenResponse login(LoginRequest request) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", "tripiz-client");
        body.add("client_secret", clientSecret);
        body.add("username", request.getUsername());
        body.add("password", request.getPassword());
        body.add("grant_type", "password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        String keycloakUrl = "https://keycloak-production-53a7.up.railway.app/realms/tripiz/protocol/openid-connect/token";

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
            throw new RuntimeException("Authentication failed: " + e.getResponseBodyAsString());
        }
    }
}