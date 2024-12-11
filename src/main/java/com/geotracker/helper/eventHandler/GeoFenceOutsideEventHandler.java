package com.geotracker.helper.eventHandler;

import com.geotracker.helper.UUIDGenerator;
import com.geotracker.model.GeoFence;
import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.VehiclePosition;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GeoFenceOutsideEventHandler extends EventHandler<GeoFenceEvent>  {

    public GeoFenceOutsideEventHandler(UUIDGenerator uuidGenerator) {
        super(uuidGenerator);
    }

    @Override
    public GeoFenceEvent handleEvent(VehiclePosition position, GeoFence geoFence, boolean isAuthorized, LocalDateTime entryTime) {
        return GeoFenceEvent.builder()
                .id(generateEventId())
                .vehicleId(position.getVehicleId())
                .build();
    }
}
