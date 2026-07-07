package com.tripiz.api.configurations;

import com.tripiz.api.domain.User;
import com.tripiz.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Connexion WebSocket refusée : header Authorization absent ou invalide");
                throw new IllegalArgumentException("Token JWT manquant");
            }

            String token = authHeader.substring(7);

            try {
                Jwt jwt = jwtDecoder.decode(token);
                String keycloakId = jwt.getSubject(); // claim "sub"

                UUID userId = userRepository.findByKeycloakId(keycloakId)
                        .map(User::getUserId)
                        .orElseThrow(() -> new IllegalStateException(
                                "Aucun User trouvé pour keycloakId=" + keycloakId));

                Principal principal = () -> userId.toString();
                accessor.setUser(principal);

            } catch (JwtException e) {
                log.warn("Connexion WebSocket refusée : token invalide ({})", e.getMessage());
                throw new IllegalArgumentException("Token JWT invalide", e);
            }
        }
        return message;
    }
}