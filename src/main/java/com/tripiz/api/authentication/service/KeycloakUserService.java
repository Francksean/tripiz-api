package com.tripiz.api.authentication.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserService {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    // Optionnel : on peut garder le client admin-cli ou utiliser directement le compte admin
    @Value("${keycloak.admin-client-id:admin-cli}")
    private String adminClientId;

    @Value("${keycloak.admin-username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin-password:admin}")
    private String adminPassword;

    public String createUser(String email, String password, String firstName, String lastName) {
        try {
            // Utiliser le compte admin pour obtenir un token (flux password)
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm("master")
                    .clientId(adminClientId)    // admin-cli
                    .username(adminUsername)
                    .password(adminPassword)
                    .grantType("password")
                    .build();

            UserRepresentation userRep = new UserRepresentation();
            userRep.setUsername(email);
            userRep.setEmail(email);
            userRep.setFirstName(firstName);
            userRep.setLastName(lastName);
            userRep.setEnabled(true);
            userRep.setEmailVerified(true);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);
            userRep.setCredentials(Collections.singletonList(credential));

            Response response = keycloak.realm(realm).users().create(userRep);
            if (response.getStatus() != 201) {
                String error = response.readEntity(String.class);
                log.error("Keycloak error {}: {}", response.getStatus(), error);
                throw new RuntimeException("Keycloak creation failed: " + error);
            }

            String location = response.getHeaderString("Location");
            String keycloakId = location.substring(location.lastIndexOf('/') + 1);
            log.info("User created in Keycloak with ID: {}", keycloakId);
            return keycloakId;

        } catch (Exception e) {
            log.error("Exception creating user in Keycloak", e);
            throw new RuntimeException("Failed to create user in Keycloak", e);
        }
    }
}