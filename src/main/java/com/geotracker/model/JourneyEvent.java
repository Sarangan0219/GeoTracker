package com.geotracker.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourneyEvent {
    private String id;
    private String vehicleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Set<String> geoFencesCrossed;
}
