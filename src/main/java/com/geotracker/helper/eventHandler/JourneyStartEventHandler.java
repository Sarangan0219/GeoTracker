package com.geotracker.helper.eventHandler;

import com.geotracker.helper.UUIDGenerator;
import com.geotracker.model.GeoFence;
import com.geotracker.model.VehiclePosition;
import com.geotracker.model.JourneyEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JourneyStartEventHandler extends EventHandler<JourneyEvent> {

    public JourneyStartEventHandler(UUIDGenerator uuidGenerator) {
        super(uuidGenerator);
    }

    @Override
    public JourneyEvent handleEvent(VehiclePosition position, GeoFence geoFence, boolean isAuthorized, LocalDateTime startTime) {
        // Handle the journey start event
        return JourneyEvent.builder()
                .id(generateEventId()) // Generate a new event ID
                .vehicleId(position.getVehicleId())
                .startTime(startTime)
                .build();
    }
}
