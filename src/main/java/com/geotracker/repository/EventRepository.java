package com.geotracker.repository;

import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.JourneyEvent;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    GeoFenceEvent saveGeoFenceEvent(GeoFenceEvent event);

    JourneyEvent saveJourneyEvent(JourneyEvent event);

    List<JourneyEvent> findJourneyByVehicleId(String vehicleId);

    Optional<GeoFenceEvent> findActiveByVehicleId(String vehicleId);

    List<GeoFenceEvent> findEventsVehicleId(String vehicleId);

    List<List<GeoFenceEvent>> findAll();

    void deleteActiveByVehicleId(String vehicleId);
}

