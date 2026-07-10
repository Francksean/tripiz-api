package com.tripiz.api.service;

import com.tripiz.api.configurations.GeoUtils;
import com.tripiz.api.configurations.StationSubscriptionTracker;
import com.tripiz.api.domain.*;
import com.tripiz.api.model.StationBusDTO;
import com.tripiz.api.model.StationBusesFeedDTO;
import com.tripiz.api.repository.ItineraryRepository;
import com.tripiz.api.repository.StationRepository;
import com.tripiz.api.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StationBusFeedService {

    private final StationSubscriptionTracker stationSubscriptionTracker;
    private final StationRepository stationRepository;
    private final ItineraryRepository itineraryRepository;
    private final TripRepository tripRepository;
    private final BusPositionService busPositionService;
    private final SimpMessagingTemplate messagingTemplate;

    // km/h, ajustable dans application.properties sans recompiler
    @Value("${tripiz.eta.average-speed-kmh:20}")
    private double vitesseMoyenneKmh;

    @Scheduled(fixedRate = 15000)
    public void diffuserBusParStation() {
        for (String stationIdStr : stationSubscriptionTracker.getActiveStationIds()) {
            UUID stationId = UUID.fromString(stationIdStr);
            calculerEtDiffuser(stationId);
        }
    }

    private void calculerEtDiffuser(UUID stationId) {
        Optional<Station> stationOpt = stationRepository.findById(stationId);
        if (stationOpt.isEmpty()) return;
        Station station = stationOpt.get();

        List<Itinerary> itineraires = itineraryRepository
                .findByDepartureStationOrArrivalStation(stationId, stationId);

        LocalDate aujourdhui = LocalDate.now();
        List<StationBusDTO> bus = itineraires.stream()
                .flatMap(itineraire -> tripRepository.findByItineraryIdAndTripDateAndTripStatusIn(
                                        itineraire.getItineraryId(),
                                        aujourdhui,
                                        List.of(TripStatus.EN_COURS)
                                ).stream()
                                .map(trip -> construireDto(trip, itineraire, station))
                )
                .filter(java.util.Objects::nonNull)
                .sorted((a, b) -> Integer.compare(a.etaSeconds(), b.etaSeconds()))
                .toList();

        messagingTemplate.convertAndSend(
                "/topic/stations/" + stationId + "/buses",
                new StationBusesFeedDTO(stationId, bus)
        );
    }

    private StationBusDTO construireDto(Trip trip, Itinerary itineraire, Station station) {
        BusPosition position = busPositionService.getPosition(trip.getBusId());
        if (position == null) return null; // bus pas encore géolocalisé, on l'exclut

        double distanceMetres = GeoUtils.distanceInMeters(
                station.getLatitude(), station.getLongitude(),
                position.getLatitude(), position.getLongitude()
        );

        double vitesseMs = (vitesseMoyenneKmh * 1000) / 3600;
        int etaSeconds = (int) Math.round(distanceMetres / vitesseMs);

        return new StationBusDTO(
                trip.getBusId(),
                itineraire.getRouteName(),
                itineraire.getDirection(),
                itineraire.getItineraryId(),
                etaSeconds,
                position.getLatitude(),
                position.getLongitude(),
                position.getHeading()
        );
    }
}