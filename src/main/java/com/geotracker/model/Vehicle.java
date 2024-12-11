package com.geotracker.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    private String vehicleId;
    private String make;
    private String model;
    private String description;
    private boolean isActive;
}

