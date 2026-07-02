package com.tripiz.api.repository;

import com.tripiz.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    long countByStatusIgnoreCase(String status);
    long countByCreatedAtAfter(LocalDateTime date);
    Optional<User> findByKeycloakId(String keycloakId);
    List<User> findByRole(String role);
    @Query("SELECT u.userId FROM User u WHERE u.role = 'driver'")
    List<UUID> findAllDriverIds();
}