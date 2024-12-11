package com.geotracker.service;

import com.geotracker.exception.ResourceNotFoundException;
import com.geotracker.helper.UUIDGenerator;
import com.geotracker.model.*;
import com.geotracker.model.request.VehiclePositionRequest;
import com.geotracker.model.view.GeoFenceEventView;
import com.geotracker.model.view.JourneyView;
import com.geotracker.repository.VehicleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class VehiclePositionService {

    private final VehicleRepository vehicleRepository;
    private final UUIDGenerator uuidGenerator;
    private final EventService eventService;
    private final GeoFenceService geoFenceService;
    private  final  VehicleService vehicleService;
    private final JourneyService journeyService;

    /**
     * Starts a journey for a vehicle.
     */
    public JourneyView startJourney(String vehicleId) {
        Vehicle vehicle = vehicleService.fetchVehicle(vehicleId);
        vehicleService.activateVehicle(vehicle);
        VehiclePosition vehiclePosition = initializeVehiclePosition(vehicleId);
        VehiclePosition savedVehiclePosition = saveVehiclePosition(vehiclePosition);
        JourneyEvent journeyEvent = journeyService.handleJourneyStartEvent(vehiclePosition);
        journeyService.saveJourneyEvent(journeyEvent);
        log.info("Started journey for vehicle with ID: {}", vehicleId);
        String message = String.format("Journey started for vehicle ID: %s at %s", journeyEvent.getVehicleId(), journeyEvent.getStartTime());
        return JourneyView.fromEntity(journeyEvent, message, null);
    }

    /**
     * Ends the journey for a vehicle.
     */
    public JourneyView endJourney(String vehicleId) {
        Vehicle vehicle = vehicleService.fetchVehicle(vehicleId);
        vehicleService.deactivateVehicle(vehicle);

        VehiclePosition position = vehicleRepository.getPositionByVehicleID(vehicleId);

        JourneyEvent startJourneyEvent = journeyService.findActiveJourneyByVehicleId(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("No active journey found for vehicle: " + vehicleId));

        JourneyEvent endJourneyEvent = journeyService.handleJourneyEndEvent(position);
        endJourneyEvent.setStartTime(startJourneyEvent.getStartTime());
        endJourneyEvent.setGeoFencesCrossed(geoFenceService.findGeoFencesCrossed(vehicleId, endJourneyEvent));

        Duration journeyDuration = journeyService.calculateJourneyDuration(endJourneyEvent);
        String message = String.format("Journey ended for vehicle ID: %s at %s. Duration: %s seconds.",
                vehicleId, endJourneyEvent.getEndTime(), journeyDuration.toSeconds());

        journeyService.saveJourneyEvent(endJourneyEvent);
        log.info("Ended journey for vehicle with ID: {}", vehicleId);

        return JourneyView.fromEntity(endJourneyEvent, message, journeyDuration);
    }

    /**
     * Processes a new vehicle position update.
     */
    public GeoFenceEventView processVehiclePosition(VehiclePositionRequest vehiclePositionRequest) {
        String vehicleId = vehiclePositionRequest.vehicleId();
        log.info("Processing vehicle position for vehicle ID: {}", vehicleId);

        Vehicle vehicle = vehicleService.fetchVehicle(vehicleId);

        if (!vehicle.isActive()) {
            log.error("A journey is not started for this vehicle: {}", vehicleId);
            throw new IllegalStateException("Vehicle journey not started");
        }

        VehiclePosition vehiclePosition = Optional.ofNullable(vehicleRepository.getPositionByVehicleID(vehicleId))
                .map(existingPosition -> updateVehiclePosition(existingPosition, vehiclePositionRequest))
                .orElseGet(() -> createNewVehiclePosition(vehiclePositionRequest));

        vehiclePosition.setRecordedTimestamp(LocalDateTime.now());
        GeoFenceEvent geoFenceEvent = eventService.handleVehiclePosition(vehiclePosition);
        if(geoFenceEvent.getGeoFenceName() != null) {
            String geoFenceId = geoFenceService.getGeoFenceByName(geoFenceEvent.getGeoFenceName()).geoFenceId();
            vehiclePosition.setGeoFenceId(geoFenceId);
            vehiclePosition.setWithinGeofence(true);
        } else{
            vehiclePosition.setWithinGeofence(false);
        }
        vehicleRepository.savePosition(vehiclePosition);
        String message = geoFenceService.generateGeoFenceMessage(geoFenceEvent);
        return GeoFenceEventView.fromEntity(
                geoFenceEvent,
                message,
                geoFenceEvent.getDurationOfStay()
        );
    }

    /**
     * Initializes a new VehiclePosition object.
     */
    private VehiclePosition initializeVehiclePosition(String vehicleId) {
        VehiclePosition vehiclePosition = new VehiclePosition();
        vehiclePosition.setId(uuidGenerator.get());
        vehiclePosition.setVehicleId(vehicleId);
        vehiclePosition.setWithinGeofence(false);
        vehiclePosition.setLatitude(0.0);
        vehiclePosition.setLongitude(0.0);
        vehiclePosition.setRecordedTimestamp(LocalDateTime.now());
        return vehiclePosition;
    }

    /**
     * Saves the VehiclePosition to the repository.
     */
    private VehiclePosition saveVehiclePosition(VehiclePosition vehiclePosition) {
        return vehicleRepository.savePosition(vehiclePosition);
    }

    private VehiclePosition updateVehiclePosition(VehiclePosition existingPosition, VehiclePositionRequest request) {
        existingPosition.setLongitude(request.longitude());
        existingPosition.setLatitude(request.latitude());
        return existingPosition;
    }

    private VehiclePosition createNewVehiclePosition(VehiclePositionRequest request) {
        VehiclePosition newPosition = VehiclePositionRequest.toEntity(request);
        newPosition.setId(uuidGenerator.get());
        return newPosition;
    }
}
