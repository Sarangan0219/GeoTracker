package com.geotracker.helper;

import com.geotracker.model.GeoFence;
import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.JourneyEvent;
import com.geotracker.model.VehiclePosition;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class EventContext {

    private GeoFenceEvent currentEvent = null;
    private UUIDGenerator uuidGenerator;

    public EventContext(UUIDGenerator uuidGenerator) {
        this.uuidGenerator = uuidGenerator;
    }

    public GeoFenceEvent handleExit(VehiclePosition position, GeoFence geoFence, LocalDateTime entryTime) {
        currentEvent = GeoFenceEvent.builder()
                .id(uuidGenerator.get())
                .vehicleId(position.getVehicleId())
                .geoFenceName(geoFence.getName())
                .entryTime(entryTime)
                .exitTime(position.getRecordedTimestamp())
                .durationOfStay(Duration.between(entryTime, position.getRecordedTimestamp()))
                .isAuthorized(true)
                .alertMessage(null)
                .build();
        return currentEvent;
    }

    public GeoFenceEvent handleEntry(VehiclePosition position, GeoFence geoFence, boolean isAuthorized) {
        currentEvent = GeoFenceEvent.builder()
                    .id(uuidGenerator.get())
                    .vehicleId(position.getVehicleId())
                    .geoFenceName(geoFence.getName())
                    .entryTime(position.getRecordedTimestamp())
                    .isAuthorized(isAuthorized)
                    .alertMessage(isAuthorized ? null : "Unauthorized entry detected")
                    .build();
        return currentEvent;
    }

    public GeoFenceEvent handleInsideGeoFence(VehiclePosition position, GeoFence geoFence) {
        currentEvent = GeoFenceEvent.builder()
                .id(uuidGenerator.get())
                .vehicleId(position.getVehicleId())
                .geoFenceName(geoFence.getName())
                .entryTime(position.getRecordedTimestamp())
                .isAuthorized(true)
                .alertMessage(null)
                .build();
        return currentEvent;
    }

    public JourneyEvent handleStartJourney(VehiclePosition position) {
        return JourneyEvent.builder()
                    .id(uuidGenerator.get())
                    .vehicleId(position.getVehicleId())
                    .startTime(position.getRecordedTimestamp())
                    .build();
    }

    public JourneyEvent handleEndJourney(VehiclePosition position) {
        return JourneyEvent.builder()
                .id(uuidGenerator.get())
                .vehicleId(position.getVehicleId())
                .startTime(position.getRecordedTimestamp())
                .endTime(LocalDateTime.now())
                .build();
    }


    public Optional<GeoFenceEvent> getCurrentEvent() {
        return Optional.ofNullable(currentEvent);
    }


    public GeoFenceEvent handleOutSideGeoFences(VehiclePosition position) {
        currentEvent = GeoFenceEvent.builder()
                .id(uuidGenerator.get())
                .vehicleId(position.getVehicleId())
                .entryTime(position.getRecordedTimestamp())
                .isAuthorized(false)
                .alertMessage(null)
                .build();
        return currentEvent;
    }
}
