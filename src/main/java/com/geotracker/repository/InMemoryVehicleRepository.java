package com.geotracker.repository;

import com.geotracker.model.Vehicle;
import com.geotracker.model.VehiclePosition;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("In-memory")
public class InMemoryVehicleRepository implements VehicleRepository {

    private final Map<String, Vehicle> vehicles = new ConcurrentHashMap<>();
    private final Map<String, VehiclePosition> vehiclePositions = new ConcurrentHashMap<>();

    @Override
    public Vehicle save(Vehicle vehicle) {
        vehicles.put(vehicle.getVehicleId(), vehicle);
        return vehicle;
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        vehicles.put(vehicle.getVehicleId(), vehicle);
        return vehicle;
    }

    @Override
    public Optional<Vehicle> findById(String vehicleId) {
        return Optional.ofNullable(vehicles.get(vehicleId));
    }

    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles.values());
    }

    @Override
    public void deleteById(String vehicleId) {
        vehicles.remove(vehicleId);
    }

    @Override
    public VehiclePosition savePosition(VehiclePosition vehiclePosition) {
        vehiclePositions.put(vehiclePosition.getVehicleId(), vehiclePosition);
        return vehiclePosition;
    }

    @Override
    public VehiclePosition getPositionByVehicleID(String vehicleId) {
        return vehiclePositions.values().stream()
                .filter(vehiclePosition -> vehiclePosition.getVehicleId().equals(vehicleId)).findFirst().orElse(null);
    }
}
