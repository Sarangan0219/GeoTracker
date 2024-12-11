package com.geotracker.helper.eventHandler;

import com.geotracker.helper.UUIDGenerator;
import com.geotracker.model.GeoFence;
import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.VehiclePosition;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;


@Component
public class GeoFenceExitEventHandler extends EventHandler<GeoFenceEvent> {

    public GeoFenceExitEventHandler(UUIDGenerator uuidGenerator) {
        super(uuidGenerator);
    }

    @Override
    public GeoFenceEvent handleEvent(VehiclePosition position, GeoFence geoFence, boolean isAuthorized, LocalDateTime entryTime) {
        LocalDateTime exitTime = position.getRecordedTimestamp();
        Duration duration = Duration.between(entryTime, exitTime);
        return GeoFenceEvent.builder()
                .id(generateEventId())
                .vehicleId(position.getVehicleId())
                .geoFenceName(geoFence.getName())
                .entryTime(entryTime)
                .exitTime(exitTime)
                .durationOfStay(duration)
                .isAuthorized(isAuthorized)
                .alertMessage(null)
                .build();
    }
}

