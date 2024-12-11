package com.geotracker.model.view;

import com.geotracker.model.GeoFence;

import java.util.Set;

public record GeoFenceView(
        String geoFenceId,
        String name,
        Set<String> polygonCoordinates,
        Set<String> authorizedVehicleIds) {
    public static GeoFenceView fromEntity(GeoFence geoFence) {
        return new GeoFenceView(
                geoFence.getGeoFenceId(),
                geoFence.getName(),
                geoFence.getPolygonCoordinates(),
                geoFence.getAuthorizedVehicleIds()
        );
    }
}
