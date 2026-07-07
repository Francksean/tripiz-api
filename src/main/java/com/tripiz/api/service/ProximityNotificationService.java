package com.tripiz.api.service;

import com.tripiz.api.domain.Bus;
import com.tripiz.api.domain.BusPosition;
import com.tripiz.api.domain.User;
import com.tripiz.api.domain.UserPosition;
import com.tripiz.api.repository.BusRepository;
import com.tripiz.api.repository.UserRepository;
import com.tripiz.api.configurations.GeoUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProximityNotificationService {

    private static final double SEUIL_METRES = 300;
    private static final Duration COOLDOWN = Duration.ofMinutes(10);

    private final BusRepository busRepository;
    private final UserRepository userRepository;

    private final BusPositionService busPositionService;
    private final UserPositionService userPositionService;

    private final NotificationService notificationService;

    private final Map<String, LocalDateTime> derniersEnvois = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 15000)
    public void verifierProximiteBusUtilisateurs() {

        List<Bus> busList = busRepository.findAll();
        List<User> userList = userRepository.findAll();

        Map<UUID, BusPosition> busPositions =
                busPositionService.getCurrentPositions()
                        .stream()
                        .collect(Collectors.toMap(
                                BusPosition::getBusId,
                                Function.identity()
                        ));

        Map<UUID, UserPosition> userPositions =
                userPositionService.getCurrentPositions()
                        .stream()
                        .collect(Collectors.toMap(
                                UserPosition::getUserId,
                                Function.identity()
                        ));

        for (Bus bus : busList) {

            BusPosition busPosition = busPositions.get(bus.getBusId());

            if (busPosition == null) {
                continue;
            }

            for (User user : userList) {

                UserPosition userPosition = userPositions.get(user.getUserId());

                if (userPosition == null) {
                    continue;
                }

                double distance = GeoUtils.distanceInMeters(
                        busPosition.getLatitude(),
                        busPosition.getLongitude(),
                        userPosition.getLatitude(),
                        userPosition.getLongitude()
                );

                if (distance <= SEUIL_METRES && peutEnvoyer(bus.getBusId(), user.getUserId())) {

                    notificationService.createAndSend(
                            user,
                            "Bus à proximité",
                            "Le bus " + bus.getBusNumber()
                                    + " est à environ "
                                    + Math.round(distance)
                                    + " m de vous."
                    );

                    marquerEnvoye(bus.getBusId(), user.getUserId());
                }
            }
        }
    }

    private boolean peutEnvoyer(UUID busId, UUID userId) {

        String cle = busId + "_" + userId;

        LocalDateTime dernier = derniersEnvois.get(cle);

        return dernier == null ||
                Duration.between(dernier, LocalDateTime.now())
                        .compareTo(COOLDOWN) > 0;
    }

    private void marquerEnvoye(UUID busId, UUID userId) {

        derniersEnvois.put(
                busId + "_" + userId,
                LocalDateTime.now()
        );
    }
}