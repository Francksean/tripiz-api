package com.tripiz.api.controllers;


import com.tripiz.api.model.SignupRequestDTO;
import com.tripiz.api.model.SignupResponseDTO;
import com.tripiz.api.model.UpdateUserRequestDTO;
import com.tripiz.api.model.UserDTO;
import com.tripiz.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserResource {

    private final UserService accountService;

    @PostMapping("/auth/signupAsClient")
    public ResponseEntity<SignupResponseDTO> signupAsClient(@RequestBody SignupRequestDTO request) {
        SignupResponseDTO response = accountService.createClientAccount(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/auth/signupAsDriver")
    public ResponseEntity<SignupResponseDTO> signupAsDriver(@RequestBody SignupRequestDTO request) {
        SignupResponseDTO response = accountService.createDriverAccount(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        accountService.updateAccount(id, updateUserRequestDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        accountService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/getAllUsers")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> user = accountService.getAllUsers();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/admin/countOnline")
    public ResponseEntity<Integer> countOnlineUsers() {
        int count = accountService.countOnlineUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/admin/countBlocked")
    public ResponseEntity<Integer> countBlockedUsers() {
        int count = accountService.countBlockedUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/admin/countTotalUsers")
    public ResponseEntity<Long> countTotalUsers() {
        long count = accountService.countTotalUsers();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/admin/countUsersCreatedThisMonth")
    public ResponseEntity<Long> countUsersCreatedThisMonth() {
        long count = accountService.countUsersCreatedThisMonth();
        return ResponseEntity.ok(count);
    }


}
