package com.geotracker.model.view;

import com.geotracker.model.GeoFenceEvent;

import java.time.Duration;
import java.time.LocalDateTime;

public record GeoFenceEventView(
        GeoFenceEvent geoFenceEvent,
        String geoFenceName,
        LocalDateTime formattedEntryTime,
        LocalDateTime formattedExitTime,
        Duration durationOfStayFormatted,
        String eventStatus,
        Boolean isViolation,
        String vehicleId,
        String alertMessage,
        String recommendation,
        String message,
        Duration durationOfJourney) {

    public static GeoFenceEventView fromEntity(GeoFenceEvent geoFenceEvent,
                                               String message,
                                               Duration durationOfJourney) {
        return new GeoFenceEventView(
                geoFenceEvent,
                geoFenceEvent.getGeoFenceName(),
                geoFenceEvent.getEntryTime(),
                geoFenceEvent.getExitTime(),
                geoFenceEvent.getDurationOfStay(),
                determineEventStatus(geoFenceEvent),
                geoFenceEvent.getIsAuthorized() != null && !geoFenceEvent.getIsAuthorized(),
                geoFenceEvent.getVehicleId(),
                geoFenceEvent.getAlertMessage(),
                generateRecommendation(geoFenceEvent),
                message,
                durationOfJourney
        );
    }

    private static String determineEventStatus(GeoFenceEvent geoFenceEvent) {
        if (geoFenceEvent.getEntryTime() != null && geoFenceEvent.getExitTime() != null) {
            return "Entered and Exited";
        } else if (geoFenceEvent.getEntryTime() != null) {
            return "Entered";
        } else if (geoFenceEvent.getExitTime() != null) {
            return "Exited";
        }
        return "Unknown";
    }

    private static String generateRecommendation(GeoFenceEvent geoFenceEvent) {
        if (geoFenceEvent.getIsAuthorized() != null && !geoFenceEvent.getIsAuthorized()) {
            return "Investigate unauthorized access.";
        }
        return "No action needed.";
    }
}
