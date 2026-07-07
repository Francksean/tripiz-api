package com.tripiz.api.service;


import com.tripiz.api.domain.UserPosition;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserPositionService {

    private final Map<UUID, UserPosition> positions = new ConcurrentHashMap<>();

    public void savePosition(UserPosition position) {
        positions.put(position.getUserId(), position);
    }

    public UserPosition getPosition(UUID userId) {
        return positions.get(userId);
    }

    public List<UserPosition> getAllPositions() {
        return new ArrayList<>(positions.values());
    }

    public List<UserPosition> getCurrentPositions() {
        return getAllPositions();
    }
    public void removePosition(UUID userId) {
        positions.remove(userId);
    }
}