package com.geotracker.service;

import com.geotracker.exception.ResourceNotFoundException;
import com.geotracker.exception.ValidationException;
import com.geotracker.helper.GeoFenceUtils;
import com.geotracker.helper.UUIDGenerator;
import com.geotracker.model.GeoFence;
import com.geotracker.model.request.GeoFenceRequest;
import com.geotracker.model.view.GeoFenceView;
import com.geotracker.repository.GeoFenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class GeoFenceService {

    private final GeoFenceRepository geoFenceRepository;
    private final UUIDGenerator uuidGenerator;
    private final VehicleService vehicleService;

    /**
     * Creates a new GeoFence.
     *
     * @param geoFenceRequest - The request body containing GeoFence details.
     * @return GeoFenceView - The view of the created GeoFence.
     */
    public GeoFenceView createGeoFence(GeoFenceRequest geoFenceRequest) {
        validateGeoFenceNameAvailability(geoFenceRequest.name());

        // Check if the polygon intersects with any existing GeoFences
        if (GeoFenceUtils.isPolygonIntersecting(geoFenceRequest.polygonCoordinates(), getAllGeoFences())) {
            throw new ValidationException("The polygon coordinates intersect with an existing geofence.");
        }

        // Validate authorized vehicle IDs
        List<String> invalidVehicleIds = validateAuthorizedVehicleIds(geoFenceRequest.authorizedVehicleIds());
        if (!invalidVehicleIds.isEmpty()) {
            throw new ValidationException("Invalid vehicle IDs: " + String.join(", ", invalidVehicleIds));
        }

        // Create and save the GeoFence
        GeoFence geoFence = GeoFenceRequest.toEntity(geoFenceRequest);
        geoFence.setGeoFenceId(uuidGenerator.generateGeofenceId());
        geoFence.setValidationStrategy("RAY_CASTING");

        GeoFence persistedGeoFence = geoFenceRepository.save(geoFence);
        log.info("Created geofence: {}", persistedGeoFence);

        return GeoFenceView.fromEntity(persistedGeoFence);
    }

    /**
     * Validates that the GeoFence name is unique.
     *
     * @param name - The name of the GeoFence.
     */
    private void validateGeoFenceNameAvailability(String name) {
        if (geoFenceRepository.findByName(name).isPresent()) {
            throw new ValidationException("GeoFence with name '" + name + "' already exists.");
        }
    }

    /**
     * Validates the vehicle IDs to ensure they are all authorized.
     *
     * @param authorizedVehicleIds - Set of authorized vehicle IDs.
     * @return List of invalid vehicle IDs.
     */
    List<String> validateAuthorizedVehicleIds(Set<String> authorizedVehicleIds) {
        return authorizedVehicleIds.stream()
                .filter(vehicleId -> {
                    try {
                        vehicleService.getVehicle(vehicleId);
                        return false; // If no exception, the ID is valid
                    } catch (IllegalArgumentException e) {
                        return true; // Invalid vehicle ID
                    }
                })
                .toList();
    }

    /**
     * Updates an existing GeoFence.
     *
     * @param name - The name of the GeoFence to update.
     * @param updatedGeoFenceRequest - The updated GeoFence details.
     * @return GeoFenceView - The view of the updated GeoFence.
     */
    public GeoFenceView updateGeoFence(String name, GeoFenceRequest updatedGeoFenceRequest) {
        GeoFence existingGeoFence = geoFenceRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("GeoFence not found: " + name));

        // If the name is changed, ensure the new name is available
        if (!existingGeoFence.getName().equals(updatedGeoFenceRequest.name())) {
            validateGeoFenceNameAvailability(updatedGeoFenceRequest.name());
        }

        // Check if the updated polygon intersects with existing GeoFences
        List<GeoFence> allGeoFences = getAllGeoFences();
        allGeoFences.remove(existingGeoFence); // Remove the current GeoFence from the check
        if (GeoFenceUtils.isPolygonIntersecting(updatedGeoFenceRequest.polygonCoordinates(), allGeoFences)) {
            throw new ValidationException("The updated polygon coordinates intersect with an existing geofence.");
        }

        // Validate vehicle IDs
        List<String> invalidVehicleIds = validateAuthorizedVehicleIds(updatedGeoFenceRequest.authorizedVehicleIds());
        if (!invalidVehicleIds.isEmpty()) {
            throw new ValidationException("Invalid vehicle IDs: " + String.join(", ", invalidVehicleIds));
        }

        // Update GeoFence details
        existingGeoFence.setName(updatedGeoFenceRequest.name());
        existingGeoFence.setPolygonCoordinates(updatedGeoFenceRequest.polygonCoordinates());
        existingGeoFence.setAuthorizedVehicleIds(updatedGeoFenceRequest.authorizedVehicleIds());

        GeoFence updatedGeoFence = geoFenceRepository.save(existingGeoFence);
        log.info("Updated geofence: {}", updatedGeoFence);

        return GeoFenceView.fromEntity(updatedGeoFence);
    }

    /**
     * Retrieves a GeoFence by its ID.
     *
     * @param geoFenceId - The ID of the GeoFence.
     * @return GeoFenceView - The view of the requested GeoFence.
     */
    public GeoFenceView getGeoFenceById(String geoFenceId) {
        GeoFence geoFence = geoFenceRepository.findById(geoFenceId)
                .orElseThrow(() -> new ResourceNotFoundException("GeoFence not found: " + geoFenceId));

        return GeoFenceView.fromEntity(geoFence);
    }

    /**
     * Retrieves a GeoFence by its name.
     *
     * @param name - The name of the GeoFence.
     * @return GeoFenceView - The view of the requested GeoFence.
     */
    public GeoFenceView getGeoFenceByName(String name) {
        GeoFence geoFence = geoFenceRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("GeoFence not found: " + name));

        return GeoFenceView.fromEntity(geoFence);
    }

    /**
     * Retrieves all GeoFences.
     *
     * @return List of GeoFenceView objects representing all GeoFences.
     */
    public List<GeoFenceView> getAllGeoFencesForView() {
        return geoFenceRepository.findAll().stream()
                .map(GeoFenceView::fromEntity)
                .toList();
    }

    /**
     * Retrieves all GeoFences from the repository.
     *
     * @return List of GeoFence entities.
     */
    public List<GeoFence> getAllGeoFences() {
        return geoFenceRepository.findAll();
    }

    /**
     * Deletes a GeoFence by its name.
     *
     * @param name - The name of the GeoFence to delete.
     */
    public void deleteGeoFence(String name) {
        GeoFence geoFence = geoFenceRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("GeoFence not found: " + name));

        geoFenceRepository.deleteByName(name);
        log.info("Deleted geofence: {}", geoFence);
    }
}
