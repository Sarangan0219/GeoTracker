package com.geotracker.controller;

import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.JourneyEvent;
import com.geotracker.service.EventService;
import com.geotracker.service.JourneyService;
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
    private final JourneyService journeyService;

    @GetMapping("/history")
    public ResponseEntity<List<List<GeoFenceEvent>>> getGeoFenceEventHistory() {
        log.info("Fetching event history for all vehicles.");
        List<List<GeoFenceEvent>> eventHistory = eventService.getEventHistory();
        return eventHistory.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(eventHistory);
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<List<List<GeoFenceEvent>>> getGeoFenceEventsByVehicleId(@PathVariable String vehicleId) {
        log.info("Fetching event history for vehicle: {}", vehicleId);
        List<List<GeoFenceEvent>> events = eventService.getGeoFenceEventsByVehicleId(vehicleId);
        return events.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(events);
    }

    @GetMapping("/journey/history")
    public ResponseEntity<List<List<JourneyEvent>>> getJourneyHistory() {
        log.info("Fetching journey history for all the vehicles");
        List<List<JourneyEvent>> journeyEvents = journeyService.findJourneyHistory();
        return journeyEvents.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(journeyEvents);
    }

    @GetMapping("/journey/{vehicleId}")
    public ResponseEntity<List<List<JourneyEvent>>> getJourneyHistoryByVehicleId(@PathVariable String vehicleId) {
        log.info("Fetching journey history for vehicle: {}", vehicleId);
        List<List<JourneyEvent>> journeyEvents = journeyService.findJourneyHistoryByVehicleId(vehicleId);
        return journeyEvents.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(journeyEvents);
    }
}
