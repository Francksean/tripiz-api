package com.tripiz.api.controllers;

import com.tripiz.api.domain.User;
import com.tripiz.api.model.UpdateUserRequestDTO;
import com.tripiz.api.model.UserDTO;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.service.UserService;
import com.tripiz.api.ticket.dto.TicketHistoryDTO;
import com.tripiz.api.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;
    private final TicketService ticketService;
    private final UserRepository userRepository;

    @GetMapping("/getById/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
       return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequestDTO dto) {
        userService.updateAccount(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/admin/countOnline")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Integer> countOnlineUsers() {
        return ResponseEntity.ok(userService.countOnlineUsers());
    }

    @GetMapping("/admin/countBlocked")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Integer> countBlockedUsers() {
        return ResponseEntity.ok(userService.countBlockedUsers());
    }

    @GetMapping("/admin/countTotalUsers")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Long> countTotalUsers() {
        return ResponseEntity.ok(userService.countTotalUsers());
    }

    @GetMapping("/admin/countCreatedThisMonth")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Long> countUsersCreatedThisMonth() {
        return ResponseEntity.ok(userService.countUsersCreatedThisMonth());
    }

    @GetMapping("/admin/drivers")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<UserDTO>> getDrivers() {
        return ResponseEntity.ok(userService.getDrivers());
    }

    @GetMapping("/getDriverById/{id}")
    public ResponseEntity<UserDTO> getDriverById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getDriverById(id));
    }

    @GetMapping("/trips/history")
    @PreAuthorize("hasRole('client') or hasRole('driver') or hasRole('admin')")
    public ResponseEntity<List<TicketHistoryDTO>> getTripHistory(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseGet(() -> {
                    String email = jwt.getClaim("email");
                    String firstName = jwt.getClaim("given_name");
                    String lastName = jwt.getClaim("family_name");
                    String preferredUsername = jwt.getClaim("preferred_username");

                    String role = "client";
                    if ("admin@tripiz.com".equals(email)) {
                        role = "admin";
                    }

                    User newUser = User.builder()
                            .keycloakId(keycloakId)
                            .email(email != null ? email : preferredUsername + "@tripiz.local")
                            .firstName(firstName != null ? firstName : "Utilisateur")
                            .lastName(lastName != null ? lastName : "")
                            .role(role)
                            .status("ONLINE")
                            .build();
                    return userRepository.save(newUser);
                });
        List<TicketHistoryDTO> history = ticketService.getTicketHistoryForUser(user.getUserId());
        return ResponseEntity.ok(history);
    }
}