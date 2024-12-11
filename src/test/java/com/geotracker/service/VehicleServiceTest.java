package com.geotracker.service;

import com.geotracker.exception.ResourceNotFoundException;
import com.geotracker.model.request.VehicleRequest;
import com.geotracker.model.view.VehicleView;
import com.geotracker.model.Vehicle;
import com.geotracker.repository.VehicleRepository;
import com.geotracker.helper.UUIDGenerator;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UUIDGenerator uuidGenerator;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle vehicle;
    private VehicleRequest vehicleRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vehicle = new Vehicle("123", "Toyota", "Corolla", "A reliable car", false);
        vehicleRequest = new VehicleRequest("Toyota", "Corolla", "A reliable car");
    }

    @AfterEach
    void tearDown() {
        vehicle = null;
        vehicleRequest = null;
    }

    @Test
    void registerVehicle() {
        when(uuidGenerator.generateVehicleId()).thenReturn("123");
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleView vehicleView = vehicleService.registerVehicle(vehicleRequest);

        assertNotNull(vehicleView);
        assertEquals("123", vehicleView.vehicleId());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void getVehicle() {
        when(vehicleRepository.findById("123")).thenReturn(Optional.of(vehicle));

        VehicleView vehicleView = vehicleService.getVehicle("123");

        assertNotNull(vehicleView);
        assertEquals("123", vehicleView.vehicleId());
        verify(vehicleRepository, times(1)).findById("123");
    }

    @Test
    void getVehicle_NotFound() {
        when(vehicleRepository.findById("999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.getVehicle("999");
        });
        assertEquals("Vehicle not found: 999", exception.getMessage());
        verify(vehicleRepository, times(1)).findById("999");
    }

    @Test
    void getAllVehicles() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));

        var vehicles = vehicleService.getAllVehicles();

        assertNotNull(vehicles);
        assertEquals(1, vehicles.size());
        assertEquals("123", vehicles.get(0).vehicleId());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    void updateVehicle() {
        VehicleRequest updatedVehicleRequest = new VehicleRequest("Honda", "Civic", "A sporty car");
        when(vehicleRepository.findById("123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleView vehicleView = vehicleService.updateVehicle("123", updatedVehicleRequest);

        assertNotNull(vehicleView);
        assertEquals("Honda", vehicleView.make());
        assertEquals("Civic", vehicleView.model());
        assertEquals("A sporty car", vehicleView.description());
        verify(vehicleRepository, times(1)).findById("123");
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void updateVehicle_NotFound() {
        VehicleRequest updatedVehicleRequest = new VehicleRequest("Honda", "Civic", "A sporty car");
        when(vehicleRepository.findById("999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.updateVehicle("999", updatedVehicleRequest);
        });
        assertEquals("Vehicle not found: 999", exception.getMessage());
        verify(vehicleRepository, times(1)).findById("999");
    }

    @Test
    void deleteVehicle() {
        when(vehicleRepository.findById("123")).thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle("123");

        verify(vehicleRepository, times(1)).deleteById("123");
    }

    @Test
    void deleteVehicle_NotFound() {
        when(vehicleRepository.findById("999")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            vehicleService.deleteVehicle("999");
        });
        assertEquals("Vehicle not found: 999", exception.getMessage());
        verify(vehicleRepository, times(1)).findById("999");
    }
}
