package com.geotracker.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeoFence {

    private String geoFenceId;

    private String name;

    private Set<String> polygonCoordinates;

    private Set<String> authorizedVehicleIds;

    private String validationStrategy;
}
