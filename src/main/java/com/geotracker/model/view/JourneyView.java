package com.geotracker.model.view;

import com.geotracker.model.JourneyEvent;

import java.time.Duration;
import java.time.LocalDateTime;

public record JourneyView(
        JourneyEvent journeyEvent,
        String message,
        Duration durationOfJourney) {
    public static JourneyView fromEntity(JourneyEvent journeyEvent, String message, Duration durationOfJourney) {
        return new JourneyView(
                journeyEvent,
                message,
                durationOfJourney
        );
    }
}

