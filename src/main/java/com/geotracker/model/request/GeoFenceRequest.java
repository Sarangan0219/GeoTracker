package com.geotracker.model.request;

import com.geotracker.model.GeoFence;

import java.util.Set;

public record GeoFenceRequest(
        String name,
        Set<String> polygonCoordinates,
        Set<String> authorizedVehicleIds) {

    public static GeoFence toEntity(GeoFenceRequest geoFenceRecord) {
        GeoFence geoFence = new GeoFence();
        geoFence.setName(geoFenceRecord.name());
        geoFence.setPolygonCoordinates(geoFenceRecord.polygonCoordinates());
        geoFence.setAuthorizedVehicleIds(geoFenceRecord.authorizedVehicleIds());
        return geoFence;
    }
}

