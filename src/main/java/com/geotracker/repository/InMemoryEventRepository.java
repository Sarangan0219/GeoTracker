package com.geotracker.repository;

import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.JourneyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("In-memory")
public class InMemoryEventRepository implements EventRepository {

    private final Map<String, List<GeoFenceEvent>> activeEvents = new ConcurrentHashMap<>();
    private final Map<String, List<JourneyEvent>> journeyHistory = new ConcurrentHashMap<>();
    private final List<List<GeoFenceEvent>> eventHistory = Collections.synchronizedList(new ArrayList<>());

    @Override
    public GeoFenceEvent saveGeoFenceEvent(GeoFenceEvent event) {
        String vehicleId = event.getVehicleId();

        if (event.getExitTime() == null) {
            // Add to active events if the exit time is null
            activeEvents.computeIfAbsent(vehicleId, id -> new ArrayList<>()).add(event);
        } else {
            // Retrieve and clear active events for this vehicle
            List<GeoFenceEvent> events = activeEvents.remove(vehicleId);
            events.add(event);
            // Add the events to event history
            eventHistory.add(Collections.unmodifiableList(events));
        }

        return event;
    }

    @Override
    public JourneyEvent saveJourneyEvent(JourneyEvent event) {
        journeyHistory.computeIfAbsent(event.getVehicleId(), id -> new ArrayList<>()).add(event);
        return event;
    }

    @Override
    public List<JourneyEvent> findJourneyByVehicleId(String vehicleId) {
        return journeyHistory.getOrDefault(vehicleId, Collections.emptyList());
    }

    @Override
    public Optional<GeoFenceEvent> findActiveByVehicleId(String vehicleId) {
        return activeEvents.getOrDefault(vehicleId, Collections.emptyList()).stream().findFirst();
    }

    @Override
    public List<GeoFenceEvent> findEventsVehicleId(String vehicleId) {
        return activeEvents.getOrDefault(vehicleId, Collections.emptyList());
    }

    @Override
    public List<List<GeoFenceEvent>> findAll() {
        synchronized (eventHistory) {
            // Return a copy to avoid concurrency issues
            return new ArrayList<>(eventHistory);
        }
    }

    @Override
    public void deleteActiveByVehicleId(String vehicleId) {
        activeEvents.remove(vehicleId);
    }
}
