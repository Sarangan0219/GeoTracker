package com.geotracker.service;

import com.geotracker.exception.ResourceNotFoundException;
import com.geotracker.exception.ValidationException;
import com.geotracker.helper.GeoFenceUtils;
import com.geotracker.helper.UUIDGenerator;
import com.geotracker.model.GeoFence;
import com.geotracker.model.Vehicle;
import com.geotracker.model.request.GeoFenceRequest;
import com.geotracker.model.view.GeoFenceView;
import com.geotracker.model.view.VehicleView;
import com.geotracker.repository.GeoFenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GeoFenceServiceTest {

    @Mock
    private GeoFenceRepository geoFenceRepository;

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private GeoFenceService geoFenceService;

    private GeoFence existingGeoFence;

    @Mock
    private UUIDGenerator uuidGenerator;

    @BeforeEach
    void setUp() {
        existingGeoFence = new GeoFence();
        existingGeoFence.setName("Test GeoFence");
        existingGeoFence.setPolygonCoordinates(Set.of("1.0,1.0", "2.0,2.0"));
        existingGeoFence.setAuthorizedVehicleIds(Set.of("vehicle1", "vehicle2"));
    }

    @Test
    void createGeoFence_Success() {
        GeoFenceRequest geoFenceRequest = new GeoFenceRequest(
                "New GeoFence",
                Set.of("1.4,1.1", "2.3,2.2"),
                Set.of("vehicle1", "vehicle2")
        );

        GeoFence geoFence = new GeoFence();
        geoFence.setGeoFenceId("123");
        geoFence.setName("New GeoFence");
        geoFence.setPolygonCoordinates(Set.of("1.4,1.1", "2.3,2.2"));
        geoFence.setAuthorizedVehicleIds(Set.of("vehicle1", "vehicle2"));

        when(geoFenceRepository.findByName(geoFenceRequest.name())).thenReturn(Optional.empty());
        when(geoFenceRepository.save(any(GeoFence.class))).thenReturn(geoFence);
        when(uuidGenerator.generateGeofenceId()).thenReturn("123");
        when(vehicleService.getVehicle(anyString())).thenReturn(VehicleView.fromEntity(new Vehicle("vehicle1", "Make", "Model", "Description", true)));


        GeoFenceView geoFenceView = geoFenceService.createGeoFence(geoFenceRequest);

        assertNotNull(geoFenceView);
        assertEquals("New GeoFence", geoFenceView.name());
        verify(geoFenceRepository, times(1)).save(any(GeoFence.class));  // Ensure save() is called once
    }


    @Test
    void createGeoFence_ValidationException_WhenPolygonIntersects() {

        GeoFenceRequest invalidRequest = new GeoFenceRequest(
                "Updated GeoFence",
                Set.of("2.0,2.0", "3.0,3.0"),
                Set.of("vehicle3")
        );

        when(geoFenceRepository.findByName(anyString())).thenReturn(Optional.empty());


        try (MockedStatic<GeoFenceUtils> geoFenceUtilsMock = mockStatic(GeoFenceUtils.class)) {
            geoFenceUtilsMock.when(() -> GeoFenceUtils.isPolygonIntersecting(anySet(), anyList()))
                    .thenReturn(true);

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                geoFenceService.createGeoFence(invalidRequest);
            });
            assertEquals("The polygon coordinates intersect with an existing geofence.", exception.getMessage());

            verify(geoFenceRepository, times(0)).save(any(GeoFence.class));
        }
    }


    @Test
    void updateGeoFence_ValidationException_WhenPolygonIntersects() {
        GeoFenceRequest invalidRequest = new GeoFenceRequest(
                "Test GeoFence",
                Set.of("2.0,2.0", "3.0,3.0"),
                Set.of("vehicle3")
        );

        when(geoFenceRepository.findByName(anyString())).thenReturn(Optional.of(existingGeoFence));

        try (MockedStatic<GeoFenceUtils> geoFenceUtilsMock = mockStatic(GeoFenceUtils.class)) {
            geoFenceUtilsMock.when(() -> GeoFenceUtils.isPolygonIntersecting(anySet(), anyList()))
                    .thenReturn(true);

            ValidationException exception = assertThrows(ValidationException.class, () -> {
                geoFenceService.updateGeoFence("Test GeoFence", invalidRequest);
            });
            assertEquals("The updated polygon coordinates intersect with an existing geofence.", exception.getMessage());
            verify(geoFenceRepository, times(0)).save(any(GeoFence.class));
        }
    }


    @Test
    void updateGeoFence_ResourceNotFoundException_WhenGeoFenceNotFound() {
        GeoFenceRequest updatedRequest = new GeoFenceRequest(
                "Updated GeoFence",
                Set.of("2.0,2.0", "3.0,3.0"),
                Set.of("vehicle3")
        );
        when(geoFenceRepository.findByName(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            geoFenceService.updateGeoFence("NonExistent GeoFence", updatedRequest);
        });
        assertEquals("GeoFence not found: NonExistent GeoFence", exception.getMessage());
        verify(geoFenceRepository, times(0)).save(any(GeoFence.class));
    }

    @Test
    void deleteGeoFence_Success() {
        when(geoFenceRepository.findByName(anyString())).thenReturn(Optional.of(existingGeoFence));

        geoFenceService.deleteGeoFence("Test GeoFence");

        verify(geoFenceRepository, times(1)).deleteByName(anyString());
    }

    @Test
    void deleteGeoFence_ResourceNotFoundException_WhenGeoFenceNotFound() {
        when(geoFenceRepository.findByName(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            geoFenceService.deleteGeoFence("NonExistent GeoFence");
        });
        assertEquals("GeoFence not found: NonExistent GeoFence", exception.getMessage());
        verify(geoFenceRepository, times(0)).deleteByName(anyString());
    }

    @Test
    void getGeoFenceById_Success() {
        GeoFence geoFence = new GeoFence();
        geoFence.setGeoFenceId("geofenceId");
        geoFence.setName("GeoFence");
        when(geoFenceRepository.findById("geofenceId")).thenReturn(Optional.of(geoFence));

        GeoFenceView geoFenceView = geoFenceService.getGeoFenceById("geofenceId");

        assertNotNull(geoFenceView);
        assertEquals("GeoFence", geoFenceView.name());
    }

    @Test
    void getGeoFenceById_ResourceNotFoundException_WhenGeoFenceNotFound() {
        when(geoFenceRepository.findById(anyString())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            geoFenceService.getGeoFenceById("NonExistent GeoFence");
        });
        assertEquals("GeoFence not found: NonExistent GeoFence", exception.getMessage());
    }
}
