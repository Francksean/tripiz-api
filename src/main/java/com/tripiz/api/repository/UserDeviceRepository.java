package com.tripiz.api.repository;

import com.tripiz.api.domain.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDeviceRepository
        extends JpaRepository<UserDevice, UUID> {

    List<UserDevice> findByUserUserId(UUID userId);

    Optional<UserDevice> findByFcmToken(String token);

}
