package com.geotracker.service;

import com.geotracker.exception.ResourceNotFoundException;
import com.geotracker.helper.UUIDGenerator;
import com.geotracker.helper.eventHandler.JourneyEndEventHandler;
import com.geotracker.helper.eventHandler.JourneyStartEventHandler;
import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.JourneyEvent;
import com.geotracker.model.Vehicle;
import com.geotracker.model.VehiclePosition;
import com.geotracker.model.request.VehiclePositionRequest;
import com.geotracker.repository.VehicleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class VehiclePositionService {

    private final VehicleRepository vehicleRepository;
    private final UUIDGenerator uuidGenerator;
    private final EventService eventService;
    private final JourneyStartEventHandler journeyStartEventHandler;
    private final JourneyEndEventHandler journeyEndEventHandler;

    /**
     * Starts a journey for a vehicle.
     *
     * @param vehicleId - The vehicle's ID to start the journey for.
     * @return VehiclePosition - The vehicle's position after starting the journey.
     */
    public VehiclePosition startJourney(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleId));

        vehicle.setActive(true);
        vehicleRepository.save(vehicle);

        VehiclePosition vehiclePosition = new VehiclePosition();
        vehiclePosition.setId(uuidGenerator.get());
        vehiclePosition.setVehicleId(vehicleId);
        vehiclePosition.setWithinGeofence(false);
        vehiclePosition.setLatitude(0.0);
        vehiclePosition.setLongitude(0.0);
        vehiclePosition.setRecordedTimestamp(LocalDateTime.now());

        VehiclePosition savedVehiclePosition = vehicleRepository.savePosition(vehiclePosition);

        // Handle journey start event
        JourneyEvent journeyEvent = journeyStartEventHandler.handleEvent(vehiclePosition, vehiclePosition.getRecordedTimestamp());
        eventService.saveJourneyEvent(journeyEvent);
        log.info("Started journey for vehicle with ID: {}", vehicleId);

        return savedVehiclePosition;
    }

    /**
     * Ends the journey for a vehicle.
     *
     * @param vehicleId - The vehicle's ID to end the journey for.
     * @return VehiclePosition - The final position of the vehicle at the end of the journey.
     */
    public VehiclePosition endJourney(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleId));

        vehicle.setActive(false);
        vehicleRepository.save(vehicle);

        VehiclePosition position = vehicleRepository.getPositionByVehicleID(vehicleId);

        // Handle journey end event
        JourneyEvent journeyEvent = journeyEndEventHandler.handleEvent(position, position.getRecordedTimestamp());
        eventService.saveJourneyEvent(journeyEvent);
        log.info("Ended journey for vehicle with ID: {}", vehicleId);

        return position;
    }

    /**
     * Processes a new vehicle position update.
     *
     * @param vehiclePositionRequest - The request body containing new vehicle position details.
     * @return GeoFenceEvent - The processed geo-fence event based on the vehicle's position.
     */
    public GeoFenceEvent processVehiclePosition(VehiclePositionRequest vehiclePositionRequest) {
        String vehicleId = vehiclePositionRequest.vehicleId();
        log.info("Processing vehicle position for vehicle ID: {}", vehicleId);

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleId));

        if (!vehicle.isActive()) {
            log.error("A journey is not started for this vehicle: {}", vehicleId);
            throw new IllegalStateException("Vehicle journey not started");
        }

        // Update or create a new position
        VehiclePosition vehiclePosition = Optional.ofNullable(vehicleRepository.getPositionByVehicleID(vehicleId))
                .map(existingPosition -> updateVehiclePosition(existingPosition, vehiclePositionRequest))
                .orElseGet(() -> createNewVehiclePosition(vehiclePositionRequest));

        vehiclePosition.setRecordedTimestamp(LocalDateTime.now());
        vehicleRepository.savePosition(vehiclePosition);

        // Generate GeoFenceEvent based on vehicle position
        return eventService.handleVehiclePosition(vehiclePosition);
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
