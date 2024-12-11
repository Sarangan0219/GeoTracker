package com.geotracker.controller;

import com.geotracker.model.request.VehicleRequest;
import com.geotracker.model.view.VehicleView;
import com.geotracker.service.VehicleService;
import com.geotracker.exception.ResourceNotFoundException;
import com.geotracker.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
@AllArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VehicleView> registerVehicle(@RequestBody @Valid VehicleRequest vehicleRequest) {
        VehicleView registeredVehicle = vehicleService.registerVehicle(vehicleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredVehicle);
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<VehicleView> getVehicle(@PathVariable String vehicleId) {
        VehicleView vehicleView = vehicleService.getVehicle(vehicleId);
        return ResponseEntity.ok(vehicleView);
    }

    @GetMapping
    public ResponseEntity<List<VehicleView>> getAllVehicles() {
        List<VehicleView> vehicles = vehicleService.getAllVehicles();
        return vehicles.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(vehicles);
    }

    @PutMapping("/{vehicleId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<VehicleView> updateVehicle(
            @PathVariable String vehicleId,
            @RequestBody @Valid VehicleRequest vehicleRequest) {
        VehicleView updatedVehicle = vehicleService.updateVehicle(vehicleId, vehicleRequest);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{vehicleId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }
}
