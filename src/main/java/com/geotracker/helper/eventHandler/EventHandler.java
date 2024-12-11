package com.geotracker.helper.eventHandler;

import com.geotracker.helper.UUIDGenerator;
import com.geotracker.model.GeoFence;
import com.geotracker.model.VehiclePosition;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@AllArgsConstructor
@Component
public abstract class EventHandler<T> {

    protected final UUIDGenerator uuidGenerator;

    public abstract T handleEvent(VehiclePosition position, GeoFence geoFence, boolean isAuthorized, LocalDateTime startTime);

    public T handleEvent(VehiclePosition position, LocalDateTime startTime) {
        return handleEvent(position, null, false, startTime);
    }

    protected String generateEventId() {
        return uuidGenerator.get();
    }
}

