package com.geotracker.controller;

import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.VehiclePosition;
import com.geotracker.model.request.VehiclePositionRequest;
import com.geotracker.model.view.GeoFenceEventView;
import com.geotracker.model.view.JourneyView;
import com.geotracker.service.VehiclePositionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/vehicle-positions")
@AllArgsConstructor
@Slf4j
public class VehiclePositionController {

    private final VehiclePositionService vehiclePositionService;

    @PostMapping("/{vehicleId}/journeys/start")
    public ResponseEntity<JourneyView> startJourney(@PathVariable String vehicleId) {
        log.info("Attempting to start journey for vehicle: {}", vehicleId);
        JourneyView journeyView = vehiclePositionService.startJourney(vehicleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(journeyView);
    }

    @PostMapping("/{vehicleId}/journeys/end")
    public ResponseEntity<JourneyView> endJourney(@PathVariable String vehicleId) {
        log.info("Attempting to end journey for vehicle: {}", vehicleId);
        JourneyView journeyView = vehiclePositionService.endJourney(vehicleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(journeyView);
    }

    @PostMapping
    public ResponseEntity<GeoFenceEventView> updateVehiclePosition(@RequestBody @Valid VehiclePositionRequest vehiclePositionRequest) {
        log.info("Updating vehicle position with details: {}", vehiclePositionRequest);
        GeoFenceEventView geoFenceEventView = vehiclePositionService.processVehiclePosition(vehiclePositionRequest);
        return ResponseEntity.ok(geoFenceEventView);
    }
}
