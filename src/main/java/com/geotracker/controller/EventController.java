package com.geotracker.controller;

import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.JourneyEvent;
import com.geotracker.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@AllArgsConstructor
@Slf4j
public class EventController {

    private final EventService eventService;

    @GetMapping("/history")
    public ResponseEntity<List<List<GeoFenceEvent>>> getEventHistory() {
        log.info("Fetching event history.");
        List<List<GeoFenceEvent>> eventHistory = eventService.getEventHistory();
        return eventHistory.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(eventHistory);
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<List<GeoFenceEvent>> getVehicleEventsHistory(@PathVariable String vehicleId) {
        log.info("Fetching event history for vehicle: {}", vehicleId);
        List<GeoFenceEvent> events = eventService.getVehicleEventsHistory(vehicleId);
        return events.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(events);
    }

    @GetMapping("/journey/{vehicleId}")
    public ResponseEntity<List<JourneyEvent>> getVehicleJourneyHistory(@PathVariable String vehicleId) {
        log.info("Fetching journey history for vehicle: {}", vehicleId);
        List<JourneyEvent> journeyEvents = eventService.getVehicleJourneyHistory(vehicleId);
        return journeyEvents.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(journeyEvents);
    }
}
