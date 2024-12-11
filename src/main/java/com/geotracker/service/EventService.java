package com.geotracker.service;

import com.geotracker.exception.ResourceNotFoundException;
import com.geotracker.helper.eventHandler.*;
import com.geotracker.model.GeoFence;
import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.VehiclePosition;
import com.geotracker.model.JourneyEvent;
import com.geotracker.repository.EventRepository;
import com.geotracker.repository.GeoFenceRepository;
import com.geotracker.strategy.GeoFenceValidationStrategyFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class EventService {

    private final GeoFenceRepository geoFenceRepository;
    private final EventRepository eventRepository;
    private final GeoFenceValidationStrategyFactory strategyFactory;

    private final GeoFenceEntryEventHandler geoFenceEntryEventHandler;
    private final GeoFenceExitEventHandler geoFenceExitEventHandler;
    private final GeoFenceInsideEventHandler geoFenceInsideEventHandler;
    private final GeoFenceOutsideEventHandler geoFenceOutsideEventHandler;

    public GeoFenceEvent handleVehiclePosition(VehiclePosition vehiclePosition) {
        if (vehiclePosition.isWithinGeofence()) {
            return processVehicleInsideGeoFence(vehiclePosition);
        } else {
            return processVehicleOutsideGeoFence(vehiclePosition);
        }
    }

    private GeoFenceEvent processVehicleInsideGeoFence(VehiclePosition vehiclePosition) {
        String geoFenceId = vehiclePosition.getGeoFenceId();
        GeoFence geoFence = geoFenceRepository.findById(geoFenceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("GeoFence with ID %s not found", geoFenceId)));

        var strategy = strategyFactory.getStrategy(geoFence);
        if (!strategy.isWithinGeofence(vehiclePosition, geoFence)) {
            return handleExit(vehiclePosition, geoFence);
        } else {
            log.info("""
                    Vehicle {} is still within GeoFence {}
                    Recorded Timestamp: {}
                    """, vehiclePosition.getVehicleId(), geoFence.getName(), vehiclePosition.getRecordedTimestamp());
            return handleInsideGeoFence(vehiclePosition, geoFence);
        }
    }

    private GeoFenceEvent processVehicleOutsideGeoFence(VehiclePosition vehiclePosition) {
        return geoFenceRepository.findAll().stream()
                .filter(geoFence -> strategyFactory.getStrategy(geoFence)
                        .isWithinGeofence(vehiclePosition, geoFence))
                .findFirst()
                .map(geoFence -> handleEntry(vehiclePosition, geoFence))
                .orElseGet(() -> handleOutsideGeoFences(vehiclePosition));
    }

    private GeoFenceEvent handleInsideGeoFence(VehiclePosition position, GeoFence geoFence) {
        log.info("""
                Handling inside event for vehicle {} in GeoFence {}
                """, position.getVehicleId(), geoFence.getName());
        GeoFenceEvent geoFenceEntryEvent = eventRepository.findActiveByVehicleId(position.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Active geofence event not found for vehicle ID: " + position.getVehicleId()));
        GeoFenceEvent geoFenceEvent = geoFenceInsideEventHandler.handleEvent(position, geoFence, true, geoFenceEntryEvent.getEntryTime());
        eventRepository.saveGeoFenceEvent(geoFenceEvent);
        return geoFenceEvent;
    }

    private GeoFenceEvent handleEntry(VehiclePosition position, GeoFence geoFence) {
        boolean isAuthorized = geoFence.getAuthorizedVehicleIds().contains(position.getVehicleId());
        log.info("""
                Handling entry event for vehicle {} in GeoFence {}. Authorized: {}
                """, position.getVehicleId(), geoFence.getName(), isAuthorized);
        GeoFenceEvent geoFenceEvent = geoFenceEntryEventHandler.handleEvent(position, geoFence, isAuthorized,
                position.getRecordedTimestamp());
        eventRepository.saveGeoFenceEvent(geoFenceEvent);
        return geoFenceEvent;
    }

    private GeoFenceEvent handleExit(VehiclePosition position, GeoFence geoFence) {
        log.info("""
                Handling exit event for vehicle {} from GeoFence {}
                """, position.getVehicleId(), geoFence.getName());
        GeoFenceEvent geoFenceEntryEvent = eventRepository.findActiveByVehicleId(position.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Active geofence event not found for vehicle ID: " + position.getVehicleId()));
        GeoFenceEvent geoFenceEvent = geoFenceExitEventHandler.handleEvent(position, geoFence, true, geoFenceEntryEvent.getEntryTime());
        eventRepository.saveGeoFenceEvent(geoFenceEvent);
        return geoFenceEvent;
    }

    private GeoFenceEvent handleOutsideGeoFences(VehiclePosition position) {
        log.info("""
                Handling vehicle {} outside all GeoFences.
                """, position.getVehicleId());//TODO
        return geoFenceOutsideEventHandler.handleEvent(position, new GeoFence(), false, null);
    }

    public List<List<GeoFenceEvent>> getEventHistory() {
        log.info("Fetching complete event history.");
        return eventRepository.findAll();
    }

    public List<GeoFenceEvent> getVehicleEventsHistory(String vehicleId) {
        log.info("Fetching events for vehicle with ID: {}", vehicleId);
        return eventRepository.findEventsVehicleId(vehicleId);
    }

    public List<JourneyEvent> getVehicleJourneyHistory(String vehicleId) {
        log.info("Fetching journey history for vehicle with ID: {}", vehicleId);
        return eventRepository.findJourneyByVehicleId(vehicleId);
    }

    public void saveJourneyEvent(JourneyEvent journeyEvent) {
        log.info("""
                Saving journey event for vehicle {} starting at {}
                """, journeyEvent.getVehicleId(), journeyEvent.getStartTime());
        eventRepository.saveJourneyEvent(journeyEvent);
    }
}
