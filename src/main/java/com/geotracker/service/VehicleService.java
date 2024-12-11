package com.geotracker.service;

import com.geotracker.exception.ResourceNotFoundException;
import com.geotracker.model.request.VehicleRequest;
import com.geotracker.model.view.VehicleView;
import com.geotracker.model.Vehicle;
import com.geotracker.repository.VehicleRepository;
import com.geotracker.helper.UUIDGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final UUIDGenerator uuidGenerator;

    /**
     * Registers a new vehicle.
     *
     * @param vehicleRequest - Request body containing vehicle details.
     * @return VehicleView with the registered vehicle details.
     */
    public VehicleView registerVehicle(VehicleRequest vehicleRequest) {
        Vehicle vehicle = VehicleRequest.toEntity(vehicleRequest);
        vehicle.setVehicleId(uuidGenerator.generateVehicleId());
        vehicle.setActive(false); // Default is inactive, to be activated manually.

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Registered vehicle with ID: {}", savedVehicle.getVehicleId());
        return VehicleView.fromEntity(savedVehicle);
    }

    /**
     * Fetches a vehicle by its ID.
     *
     * @param vehicleId - The ID of the vehicle to fetch.
     * @return VehicleView with the vehicle details.
     */
    public VehicleView getVehicle(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleId));
        return VehicleView.fromEntity(vehicle);
    }

    /**
     * Fetches all vehicles.
     *
     * @return List of VehicleView representing all vehicles.
     */
    public List<VehicleView> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return vehicles.stream().map(VehicleView::fromEntity).toList();
    }

    /**
     * Updates the details of a vehicle.
     *
     * @param vehicleId            - The ID of the vehicle to update.
     * @param updatedVehicleRequest - The updated vehicle details.
     * @return VehicleView with the updated vehicle details.
     */
    public VehicleView updateVehicle(String vehicleId, VehicleRequest updatedVehicleRequest) {
        Vehicle existingVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found: " + vehicleId));

        existingVehicle.setMake(updatedVehicleRequest.make());
        existingVehicle.setModel(updatedVehicleRequest.model());
        existingVehicle.setDescription(updatedVehicleRequest.description());

        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
        log.info("Updated vehicle with ID: {}", updatedVehicle.getVehicleId());
        return VehicleView.fromEntity(updatedVehicle);
    }

    /**
     * Deletes a vehicle by its ID.
     *
     * @param vehicleId - The ID of the vehicle to delete.
     */
    public void deleteVehicle(String vehicleId) {
        if (vehicleRepository.findById(vehicleId).isEmpty()) {
            throw new ResourceNotFoundException("Vehicle not found: " + vehicleId);
        }

        vehicleRepository.deleteById(vehicleId);
        log.info("Deleted vehicle with ID: {}", vehicleId);
    }
}
