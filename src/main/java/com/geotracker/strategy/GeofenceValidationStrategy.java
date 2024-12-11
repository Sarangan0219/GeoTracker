package com.geotracker.strategy;

import com.geotracker.model.GeoFence;
import com.geotracker.model.VehiclePosition;

public interface GeofenceValidationStrategy {
    boolean isWithinGeofence(VehiclePosition position, GeoFence geofence);
}
