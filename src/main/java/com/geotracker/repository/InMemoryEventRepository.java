package com.geotracker.repository;

import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.JourneyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Profile("In-memory")
public class InMemoryEventRepository implements EventRepository {

    private final Map<String, List<GeoFenceEvent>> activeEvents = new ConcurrentHashMap<>();
    private final List<List<GeoFenceEvent>> eventHistory = Collections.synchronizedList(new ArrayList<>());

    private final Map<String, List<JourneyEvent>> activeJourney = new ConcurrentHashMap<>();
    private final List<List<JourneyEvent>>  journeyHistory = Collections.synchronizedList(new ArrayList<>());

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
    public Optional<GeoFenceEvent> findActiveByVehicleId(String vehicleId) {
        return activeEvents.getOrDefault(vehicleId, Collections.emptyList()).stream().findFirst();
    }

    @Override
    public List<GeoFenceEvent> findActiveEventsByVehicleId(String vehicleId) {
        return activeEvents.getOrDefault(vehicleId, Collections.emptyList());
    }

    @Override
    public List<List<GeoFenceEvent>> findAll() {
        synchronized (eventHistory) {
            return new ArrayList<>(eventHistory);
        }
    }

    @Override
    public List<List<GeoFenceEvent>> findEventsByVehicleId(String vehicleId) {
        return eventHistory.stream()
                .filter(events -> events.stream().anyMatch(event -> event.getVehicleId().equals(vehicleId)))
                .collect(Collectors.toList());
    }

    @Override
    public JourneyEvent saveJourneyEvent(JourneyEvent event) {

        String vehicleId = event.getVehicleId();

        if (event.getEndTime() == null) {
            activeJourney.computeIfAbsent(event.getVehicleId(), id -> new ArrayList<>()).add(event);
        } else {
            List<JourneyEvent> events = activeJourney.remove(vehicleId);
            events.add(event);
            journeyHistory.add(Collections.unmodifiableList(events));
        }
        return event;
    }

    @Override
    public JourneyEvent findActiveJourneyByVehicleId(String vehicleId) {
        return Optional.ofNullable(activeJourney.get(vehicleId))
                .stream()
                .flatMap(List::stream)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<List<JourneyEvent>> findJourneyHistory() {
        return journeyHistory;
    }

    @Override
    public List<List<JourneyEvent>> findJourneyHistoryByVehicleId(String vehicleId) {
        return journeyHistory.stream()
                .filter(events -> events.stream().anyMatch(event -> event.getVehicleId().equals(vehicleId)))
                .collect(Collectors.toList());
    }
}
