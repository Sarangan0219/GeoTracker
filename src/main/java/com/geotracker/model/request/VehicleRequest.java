package com.geotracker.model.request;

import com.geotracker.model.Vehicle;

public record VehicleRequest(String make, String model, String description) {
    public static Vehicle toEntity(VehicleRequest request) {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(request.make());
        vehicle.setModel(request.model());
        vehicle.setDescription(request.description());
        return vehicle;
    }
}
