package com.geotracker.repository;

import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.JourneyEvent;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    GeoFenceEvent saveGeoFenceEvent(GeoFenceEvent event);

    List<List<GeoFenceEvent>> findEventsByVehicleId(String vehicleId);

    JourneyEvent saveJourneyEvent(JourneyEvent event);

    JourneyEvent findActiveJourneyByVehicleId(String vehicleId);

    List<List<JourneyEvent>> findJourneyHistory();
    List<List<JourneyEvent>> findJourneyHistoryByVehicleId(String vehicleId);

    Optional<GeoFenceEvent> findActiveByVehicleId(String vehicleId);

    List<GeoFenceEvent> findActiveEventsByVehicleId(String vehicleId);

    List<List<GeoFenceEvent>> findAll();
}

