package com.tripiz.api.service;

import com.tripiz.api.domain.User;
import com.tripiz.api.model.UpdateUserRequestDTO;
import com.tripiz.api.model.UserDTO;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public void updateAccount(UUID id, UpdateUserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUserFromDto(dto, user);
        userRepository.save(user);
    }

    public List<UserDTO> getDrivers() {
        List<User> drivers = userRepository.findByRole("driver");
        return drivers.stream().map(userMapper::toUserDTO).collect(Collectors.toList());
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.isEmpty() ? Collections.emptyList() : users.stream().map(userMapper::toUserDTO).toList();
    }

    public int countOnlineUsers() {
        return (int) userRepository.countByStatusIgnoreCase("ONLINE");
    }

    public int countBlockedUsers() {
        return (int) userRepository.countByStatusIgnoreCase("BLOCKED");
    }

    public Long countTotalUsers() {
        return userRepository.count();
    }

    public long countUsersCreatedThisMonth() {
        return userRepository.countByCreatedAtAfter(LocalDate.now().withDayOfMonth(1).atStartOfDay());
    }
}