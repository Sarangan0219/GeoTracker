package com.geotracker.strategy;

import com.geotracker.model.GeoFence;
import com.geotracker.model.VehiclePosition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RayCastingGeofenceValidationStrategyTest {

    private RayCastingGeofenceValidationStrategy rayCastingGeofenceValidationStrategy;

    @BeforeEach
    void setUp() {
        rayCastingGeofenceValidationStrategy = new RayCastingGeofenceValidationStrategy();
    }

    @Test
    void testPointInsideGeofence() {
        GeoFence geoFence = Mockito.mock(GeoFence.class);
        Set<String> polygonCoordinates = Set.of("(0, 0)", "(0, 10)", "(10, 10)", "(10, 0)");
        Mockito.when(geoFence.getPolygonCoordinates()).thenReturn(polygonCoordinates);

        VehiclePosition position = new VehiclePosition();
        position.setLatitude(5.0);
        position.setLongitude(5.0);

        boolean result = rayCastingGeofenceValidationStrategy.isWithinGeofence(position, geoFence);

        assertTrue(result);
    }

    @Test
    void testPointOutsideGeofence() {
        GeoFence geoFence = Mockito.mock(GeoFence.class);
        Set<String> polygonCoordinates = Set.of("(0, 0)", "(0, 10)", "(10, 10)", "(10, 0)");
        Mockito.when(geoFence.getPolygonCoordinates()).thenReturn(polygonCoordinates);

        VehiclePosition position = new VehiclePosition();
        position.setLatitude(15.0);
        position.setLongitude(15.0);

        boolean result = rayCastingGeofenceValidationStrategy.isWithinGeofence(position, geoFence);

        assertFalse(result);
    }

    @Test
    void testPointOnGeofenceBoundary() {
        GeoFence geoFence = Mockito.mock(GeoFence.class);
        Set<String> polygonCoordinates = Set.of("(0, 0)", "(0, 10)", "(10, 10)", "(10, 0)");
        Mockito.when(geoFence.getPolygonCoordinates()).thenReturn(polygonCoordinates);

        VehiclePosition position = new VehiclePosition();
        position.setLatitude(0.0);
        position.setLongitude(5.0);

        boolean result = rayCastingGeofenceValidationStrategy.isWithinGeofence(position, geoFence);

        assertTrue(result);
    }

    @Test
    void testPointOnVertex() {
        GeoFence geoFence = Mockito.mock(GeoFence.class);
        Set<String> polygonCoordinates = Set.of("(0, 0)", "(0, 10)", "(10, 10)", "(10, 0)");
        Mockito.when(geoFence.getPolygonCoordinates()).thenReturn(polygonCoordinates);

        VehiclePosition position = new VehiclePosition();
        position.setLatitude(0.0);
        position.setLongitude(0.0);

        boolean result = rayCastingGeofenceValidationStrategy.isWithinGeofence(position, geoFence);

        assertTrue(result);
    }

    @Test
    void testEmptyPolygonCoordinates() {
        GeoFence geoFence = Mockito.mock(GeoFence.class);
        Set<String> polygonCoordinates = Set.of();
        Mockito.when(geoFence.getPolygonCoordinates()).thenReturn(polygonCoordinates);

        VehiclePosition position = new VehiclePosition();
        position.setLatitude(5.0);
        position.setLongitude(5.0);

        boolean result = rayCastingGeofenceValidationStrategy.isWithinGeofence(position, geoFence);

        assertFalse(result);
    }
}
