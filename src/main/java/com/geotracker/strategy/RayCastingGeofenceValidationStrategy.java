package com.geotracker.strategy;

import com.geotracker.model.GeoFence;
import com.geotracker.model.VehiclePosition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RayCastingGeofenceValidationStrategy implements GeofenceValidationStrategy {

    @Override
    public boolean isWithinGeofence(VehiclePosition position, GeoFence geoFence) {
        // Convert polygon coordinates from Set<String> to List<double[]>
        List<double[]> polygonCoordinates = convertToCoordinates(geoFence.getPolygonCoordinates());

        // The point to check (vehicle position)
        double px = position.getLongitude();
        double py = position.getLatitude();

        return isPointInPolygon(px, py, polygonCoordinates);
    }

    /**
     * Converts a Set of polygon coordinates (String) into a List of double[] coordinates.
     * Assumes coordinates are in "latitude,longitude" format.
     */
    private List<double[]> convertToCoordinates(Set<String> polygonCoordinates) {
        return polygonCoordinates.stream().map(coord -> {
            String cleanedCoord = coord.replace("(", "").replace(")", "").trim();
            String[] parts = cleanedCoord.split(",");
            double lat = Double.parseDouble(parts[0].trim());
            double lon = Double.parseDouble(parts[1].trim());
            return new double[]{lon, lat}; // Coordinates are stored as [longitude, latitude]
        }).collect(Collectors.toList());
    }


    /**
     * Determines if a point (px, py) is inside the polygon using Ray Casting algorithm.
     */
    private boolean isPointInPolygon(double px, double py, List<double[]> polygonCoordinates) {
        int n = polygonCoordinates.size();
        boolean inside = false;

        // Iterate over each edge of the polygon
        for (int i = 0; i < n; i++) {
            double[] v1 = polygonCoordinates.get(i); // Start point of edge
            double[] v2 = polygonCoordinates.get((i + 1) % n); // End point of edge (loop around)

            double x1 = v1[0], y1 = v1[1]; // Coordinate of first vertex of edge
            double x2 = v2[0], y2 = v2[1]; // Coordinate of second vertex of edge

            // Check if the point (px, py) intersects with the edge (x1, y1) -> (x2, y2)
            if (rayIntersectsSegment(px, py, x1, y1, x2, y2)) {
                inside = !inside; // Flip the "inside" flag on each intersection
            }
        }

        return inside;
    }

    /**
     * Helper method to determine if a ray from (px, py) intersects the segment (x1, y1) -> (x2, y2)
     */
    private boolean rayIntersectsSegment(double px, double py, double x1, double y1, double x2, double y2) {
        // Ensure y1 <= y2 for simplicity
        if (y1 > y2) {
            double tempX = x1;
            double tempY = y1;
            x1 = x2;
            y1 = y2;
            x2 = tempX;
            y2 = tempY;
        }

        // Check if the ray is within the vertical bounds of the segment
        if (py == y1 || py == y2) {
            py += 0.0000001; // Avoid floating-point issues where the point lies exactly on a vertex
        }

        if (py < y1 || py > y2) {
            return false; // The ray does not intersect this segment vertically
        }

        // Compute the x-coordinate of the intersection point
        double intersectX = (py - y1) * (x2 - x1) / (y2 - y1) + x1;

        // If the intersection point is to the right of px, the ray intersects
        return intersectX > px;
    }
}
