package com.geotracker.model.view;

import com.geotracker.model.Vehicle;

public record VehicleView(String vehicleId, String make, String model, String description) {

    public static VehicleView fromEntity(Vehicle vehicle) {
        return new VehicleView(
                vehicle.getVehicleId(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getDescription()
        );
    }
}

