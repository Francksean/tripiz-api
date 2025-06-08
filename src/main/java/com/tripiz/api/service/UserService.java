package com.tripiz.api.service;


import com.tripiz.api.domain.User;
import com.tripiz.api.model.SignupRequestDTO;
import com.tripiz.api.model.SignupResponseDTO;
import com.tripiz.api.model.UpdateUserRequestDTO;
import com.tripiz.api.model.UserDTO;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public SignupResponseDTO createClientAccount(SignupRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String password = request.getPassword();
        if (!password.matches("^(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            throw new IllegalArgumentException("Invalid Password format");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toSignupResponseDTO(savedUser);
    }


    public SignupResponseDTO createDriverAccount(SignupRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        String password = request.getPassword();
        if (!password.matches("^(?=.*\\d)[A-Za-z\\d]{8,}$")) {
            throw new IllegalArgumentException("Invalid Password format");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
        user.setRole("driver");

        User savedUser = userRepository.save(user);
        return userMapper.toSignupResponseDTO(savedUser);
    }

    public void updateAccount(UUID id, UpdateUserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUserFromDto(dto, user);

        userRepository.save(user);
    }

    public void deleteUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream().map(userMapper::toUserDTO).toList();
    }
}
