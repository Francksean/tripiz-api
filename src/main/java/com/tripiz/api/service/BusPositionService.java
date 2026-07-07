package com.tripiz.api.service;


import com.tripiz.api.domain.BusPosition;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BusPositionService {

    private final Map<UUID, BusPosition> positions = new ConcurrentHashMap<>();

    public void savePosition(BusPosition position) {
        positions.put(position.getBusId(), position);
    }

    public BusPosition getPosition(UUID busId) {
        return positions.get(busId);
    }

    public List<BusPosition> getAllPositions() {
        return new ArrayList<>(positions.values());
    }

    public List<BusPosition> getCurrentPositions() {
        return getAllPositions();
    }

    public void removePosition(UUID busId) {
        positions.remove(busId);
    }
}