package com.geotracker.service;

import com.geotracker.exception.ResourceNotFoundException;
import com.geotracker.helper.UUIDGenerator;
import com.geotracker.helper.eventHandler.JourneyEndEventHandler;
import com.geotracker.helper.eventHandler.JourneyStartEventHandler;
import com.geotracker.model.GeoFenceEvent;
import com.geotracker.model.JourneyEvent;
import com.geotracker.model.Vehicle;
import com.geotracker.model.VehiclePosition;
import com.geotracker.model.request.VehiclePositionRequest;
import com.geotracker.repository.VehicleRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class VehiclePositionServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UUIDGenerator uuidGenerator;

    @Mock
    private EventService eventService;

    @Mock
    private JourneyStartEventHandler journeyStartEventHandler;

    @Mock
    private JourneyEndEventHandler journeyEndEventHandler;

    @InjectMocks
    private VehiclePositionService vehiclePositionService;

    private Vehicle vehicle;
    private VehiclePositionRequest vehiclePositionRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        vehicle = new Vehicle("123", "Toyota", "Corolla", "A reliable car", false);
        vehiclePositionRequest = new VehiclePositionRequest("123", 12.34, 56.78);
    }

    @Test
    void startJourney_Success() {
        when(vehicleRepository.findById("123")).thenReturn(Optional.of(vehicle));
        when(uuidGenerator.get()).thenReturn("456");
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
        VehiclePosition mockedVehiclePosition = new VehiclePosition();
        mockedVehiclePosition.setId("456");
        when(vehicleRepository.savePosition(any(VehiclePosition.class))).thenReturn(mockedVehiclePosition);
        JourneyEvent journeyEvent = mock(JourneyEvent.class);
        when(journeyStartEventHandler.handleEvent(any(), any())).thenReturn(journeyEvent);
        doNothing().when(eventService).saveJourneyEvent(any(JourneyEvent.class));
        VehiclePosition vehiclePosition = vehiclePositionService.startJourney("123");
        assertNotNull(vehiclePosition);
        assertEquals("456", vehiclePosition.getId());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        verify(journeyStartEventHandler, times(1)).handleEvent(any(), any());
        verify(eventService, times(1)).saveJourneyEvent(any(JourneyEvent.class));
    }

    @Test
    void startJourney_VehicleNotFound() {
        when(vehicleRepository.findById("999")).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            vehiclePositionService.startJourney("999");
        });
        assertEquals("Vehicle not found: 999", exception.getMessage());
        verify(vehicleRepository, times(1)).findById("999");
    }

    @Test
    void endJourney_Success() {
        when(vehicleRepository.findById("123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.getPositionByVehicleID("123")).thenReturn(new VehiclePosition());
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
        JourneyEvent journeyEvent = mock(JourneyEvent.class);
        when(journeyEndEventHandler.handleEvent(any(), any())).thenReturn(journeyEvent);
        doNothing().when(eventService).saveJourneyEvent(any(JourneyEvent.class));
        VehiclePosition vehiclePosition = vehiclePositionService.endJourney("123");
        assertNotNull(vehiclePosition);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
        verify(journeyEndEventHandler, times(1)).handleEvent(any(), any());
        verify(eventService, times(1)).saveJourneyEvent(any(JourneyEvent.class));
    }

    @Test
    void endJourney_VehicleNotFound() {
        when(vehicleRepository.findById("999")).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            vehiclePositionService.endJourney("999");
        });
        assertEquals("Vehicle not found: 999", exception.getMessage());
        verify(vehicleRepository, times(1)).findById("999");
    }

    @Test
    void processVehiclePosition_Success() {
        vehicle.setActive(true);
        when(vehicleRepository.findById("123")).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.getPositionByVehicleID("123")).thenReturn(new VehiclePosition());
        when(vehicleRepository.savePosition(any(VehiclePosition.class))).thenReturn(new VehiclePosition());
        GeoFenceEvent geoFenceEvent = mock(GeoFenceEvent.class);
        when(eventService.handleVehiclePosition(any(VehiclePosition.class))).thenReturn(geoFenceEvent);
        GeoFenceEvent result = vehiclePositionService.processVehiclePosition(vehiclePositionRequest);
        assertNotNull(result);
        verify(vehicleRepository, times(1)).savePosition(any(VehiclePosition.class));
        verify(eventService, times(1)).handleVehiclePosition(any(VehiclePosition.class));
    }

    @Test
    void processVehiclePosition_VehicleNotFound() {
        when(vehicleRepository.findById("999")).thenReturn(Optional.empty());
        VehiclePositionRequest vehiclePositionRequest = new VehiclePositionRequest("999", 2.2, 3.3);
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            vehiclePositionService.processVehiclePosition(vehiclePositionRequest);
        });
        assertEquals("Vehicle not found: 999", exception.getMessage());
        verify(vehicleRepository, times(1)).findById("999");
    }

    @Test
    void processVehiclePosition_VehicleNotActive() {
        when(vehicleRepository.findById("123")).thenReturn(Optional.of(new Vehicle("123", "Toyota", "Corolla", "A reliable car", false)));
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            vehiclePositionService.processVehiclePosition(vehiclePositionRequest);
        });
        assertEquals("Vehicle journey not started", exception.getMessage());
    }
}
