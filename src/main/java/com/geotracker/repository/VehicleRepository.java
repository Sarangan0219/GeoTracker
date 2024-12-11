package com.geotracker.repository;

import com.geotracker.model.Vehicle;
import com.geotracker.model.VehiclePosition;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {
    Vehicle save(Vehicle vehicle);

    Vehicle update(Vehicle vehicle);

    Optional<Vehicle> findById(String vehicleId);

    List<Vehicle> findAll();

    void deleteById(String vehicleId);

    VehiclePosition savePosition(VehiclePosition vehiclePosition);

    VehiclePosition getPositionByVehicleID(String vehicleId);
}
