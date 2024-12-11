package com.geotracker.service;

import com.geotracker.exception.ResourceNotFoundException;
import com.geotracker.helper.eventHandler.JourneyEndEventHandler;
import com.geotracker.helper.eventHandler.JourneyStartEventHandler;
import com.geotracker.model.JourneyEvent;
import com.geotracker.model.Vehicle;
import com.geotracker.model.VehiclePosition;
import com.geotracker.model.view.JourneyView;
import com.geotracker.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class JourneyService {
    private final EventService eventService;
    private final JourneyStartEventHandler journeyStartEventHandler;
    private final JourneyEndEventHandler journeyEndEventHandler;
    private final EventRepository eventRepository;

    public JourneyEvent startJourneyEvent(VehiclePosition vehiclePosition) {
        return journeyStartEventHandler.handleEvent(vehiclePosition, vehiclePosition.getRecordedTimestamp());
    }

    public JourneyEvent endJourneyEvent(VehiclePosition position, String vehicleId, LocalDateTime now) {
        JourneyEvent startJourneyEvent = findActiveJourneyByVehicleId(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("No active journey found for vehicle: " + vehicleId));
        JourneyEvent endJourneyEvent = journeyEndEventHandler.handleEvent(position, now);
        endJourneyEvent.setStartTime(startJourneyEvent.getStartTime());
        return endJourneyEvent;
    }
    public Duration calculateJourneyDuration(JourneyEvent endJourneyEvent) {
        LocalDateTime journeyStartTime = findActiveJourneyByVehicleId(endJourneyEvent.getVehicleId()).get().getStartTime();
        return Duration.between(journeyStartTime, endJourneyEvent.getEndTime());
    }

    public JourneyEvent handleJourneyStartEvent(VehiclePosition vehiclePosition) {
        return journeyStartEventHandler.handleEvent(vehiclePosition, vehiclePosition.getRecordedTimestamp());
    }

    public JourneyEvent handleJourneyEndEvent(VehiclePosition vehiclePosition) {
        return journeyEndEventHandler.handleEvent(vehiclePosition, LocalDateTime.now());
    }

    public List<List<JourneyEvent>> findJourneyHistory() {
        return eventRepository.findJourneyHistory() ;
    }

    public Optional<JourneyEvent> findActiveJourneyByVehicleId(String vehicleId) {
        log.info("Fetching journey history for vehicle with ID: {}", vehicleId);
        return Optional.ofNullable(eventRepository.findActiveJourneyByVehicleId(vehicleId));
    }

    public List<List<JourneyEvent>> findJourneyHistoryByVehicleId(String vehicleId) {
        log.info("Fetching journey history for vehicle with ID: {}", vehicleId);
        return eventRepository.findJourneyHistoryByVehicleId(vehicleId);
    }

    public void saveJourneyEvent(JourneyEvent journeyEvent) {
        log.info("""
                Saving journey event for vehicle {} starting at {}
                """, journeyEvent.getVehicleId(), journeyEvent.getStartTime());
        eventRepository.saveJourneyEvent(journeyEvent);
    }

}
