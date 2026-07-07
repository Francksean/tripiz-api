package com.tripiz.api.controllers;

import com.tripiz.api.domain.UserPosition;
import com.tripiz.api.service.UserPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Controller
public class UserPositionResource {

    private final UserPositionService userPositionService;

    /**
     * Contrairement au bus, on ne diffuse PAS la position d'un utilisateur sur un topic public
     * (/topic/...) : c'est une donnée privée, elle ne sert qu'en interne côté serveur pour
     * calculer la proximité avec les bus. D'où l'absence de @SendTo ici.
     */
    @MessageMapping("/user/position")
    public void updatePosition(
            @Payload UserPosition position,
            SimpMessageHeaderAccessor headerAccessor) {

        UUID userId = resolveUserId(headerAccessor);

        // Sécurité : comme pour le bus, on ignore le userId envoyé dans le payload
        // et on lui substitue celui déduit du Principal authentifié.
        position.setUserId(userId);

        userPositionService.savePosition(position);
    }

    private UUID resolveUserId(SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();

        if (principal == null) {
            throw new IllegalStateException("Connexion WebSocket non authentifiée : aucun Principal trouvé");
        }

        return UUID.fromString(principal.getName());
    }

    @GetMapping("/user/{id}/position")
    public ResponseEntity<UserPosition> getPosition(
            @PathVariable UUID id) {

        UserPosition position = userPositionService.getPosition(id);

        if (position == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(position);
    }

    @GetMapping("/user/positions")
    public ResponseEntity<List<UserPosition>> getAllPositions() {
        return ResponseEntity.ok(userPositionService.getAllPositions());
    }
}