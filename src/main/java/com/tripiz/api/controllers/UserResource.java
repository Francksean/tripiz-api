package com.tripiz.api.controllers;

import com.tripiz.api.model.UpdateUserRequestDTO;
import com.tripiz.api.model.UserDTO;
import com.tripiz.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserResource {

    private final UserService userService;

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
}