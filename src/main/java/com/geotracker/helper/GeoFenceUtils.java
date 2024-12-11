package com.geotracker.helper;

import com.geotracker.model.GeoFence;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GeoFenceUtils {

    public static boolean isPolygonIntersecting(Set<String> newPolygonCoordinates, List<GeoFence> existingGeoFences) {

        Set<String> newPolygonBoundingBox = getBoundingBox(newPolygonCoordinates);

        for (GeoFence existingGeoFence : existingGeoFences) {
            Set<String> existingBoundingBox = getBoundingBox(existingGeoFence.getPolygonCoordinates());

            if (newPolygonBoundingBox.equals(existingBoundingBox)) {
                return true;
            }
        }
        return false;
    }


    private static Set<String> getBoundingBox(Set<String> polygonCoordinates) {
        return polygonCoordinates.stream()
                .map(coord -> coord.split(","))
                .flatMap(parts -> Set.of(parts[0], parts[1]).stream())
                .collect(Collectors.toSet());
    }
}
