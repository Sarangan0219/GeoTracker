package com.geotracker.strategy;

import com.geotracker.model.GeoFence;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GeoFenceValidationStrategyFactory {

    private final Map<String, GeofenceValidationStrategy> strategyMap = new HashMap<>();

    public GeoFenceValidationStrategyFactory(
            RayCastingGeofenceValidationStrategy rayCastingStrategy) {
        strategyMap.put("RAY_CASTING", rayCastingStrategy);
    }

    public GeofenceValidationStrategy getStrategy(GeoFence geofence) {
        String validationStrategy = geofence.getValidationStrategy(); // Assume you have this field in the geofence model
        return strategyMap.getOrDefault(validationStrategy, strategyMap.get("RAY_CASTING")); // Default to Ray Casting
    }
}
