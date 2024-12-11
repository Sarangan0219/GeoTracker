package com.geotracker.helper;

import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.function.Supplier;

@Component
public class UUIDGenerator  implements Supplier<String> {
    @Override
    public String get() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a UUID with a prefix for Vehicle.
     *
     * @return a unique ID with the "VEH-" prefix.
     */
    public String generateVehicleId() {
        return "VEH-" + get();
    }

    /**
     * Generates a UUID with a prefix for Geofence.
     *
     * @return a unique ID with the "GEO-" prefix.
     */
    public String generateGeofenceId() {
        return "GEO-" + get();
    }


}
