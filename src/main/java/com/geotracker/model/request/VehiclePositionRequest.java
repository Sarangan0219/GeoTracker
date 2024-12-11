package com.geotracker.model.request;

import com.geotracker.model.Vehicle;
import com.geotracker.model.VehiclePosition;

public record VehiclePositionRequest(String vehicleId, double latitude, double longitude) {

    public static VehiclePosition toEntity( VehiclePositionRequest request) {
        VehiclePosition vehiclePosition = new VehiclePosition();
        vehiclePosition.setLatitude(request.latitude());
        vehiclePosition.setLongitude(request.longitude());
        vehiclePosition.setVehicleId(request.vehicleId());
        return vehiclePosition;
    }
}
