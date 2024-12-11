package com.geotracker.controller;

import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.VehiclePosition;
import com.geotracker.model.request.VehiclePositionRequest;
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
    public ResponseEntity<VehiclePosition> startJourney(@PathVariable String vehicleId) {
        log.info("Attempting to start journey for vehicle: {}", vehicleId);
        VehiclePosition vehiclePosition = vehiclePositionService.startJourney(vehicleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiclePosition);
    }

    @PostMapping("/{vehicleId}/journeys/end")
    public ResponseEntity<VehiclePosition> endJourney(@PathVariable String vehicleId) {
        log.info("Attempting to end journey for vehicle: {}", vehicleId);
        VehiclePosition vehiclePosition = vehiclePositionService.endJourney(vehicleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiclePosition);
    }

    @PostMapping
    public ResponseEntity<GeoFenceEvent> updateVehiclePosition(@RequestBody @Valid VehiclePositionRequest vehiclePositionRequest) {
        log.info("Updating vehicle position with details: {}", vehiclePositionRequest);
        GeoFenceEvent geoFenceEvent = vehiclePositionService.processVehiclePosition(vehiclePositionRequest);
        return ResponseEntity.ok(geoFenceEvent);
    }
}
