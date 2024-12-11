package com.geotracker.model;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoFenceEvent {

    private String id;
    private String vehicleId;
    private String geoFenceName;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Boolean isAuthorized;
    private String alertMessage;
    private Duration durationOfStay;
}
