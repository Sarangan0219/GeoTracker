package com.geotracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiclePosition {

    private String id;
    private String vehicleId;
    private String geoFenceId;
    private Double latitude;
    private Double longitude;
    private boolean isWithinGeofence;
    private LocalDateTime recordedTimestamp;
}
