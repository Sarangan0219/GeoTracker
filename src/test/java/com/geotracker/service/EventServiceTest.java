package com.geotracker.service;

import com.geotracker.helper.eventHandler.*;
import com.geotracker.model.*;
import com.geotracker.repository.EventRepository;
import com.geotracker.repository.GeoFenceRepository;
import com.geotracker.strategy.GeoFenceValidationStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private GeoFenceRepository geoFenceRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private GeoFenceValidationStrategyFactory strategyFactory;

    @Mock
    private GeoFenceEntryEventHandler geoFenceEntryEventHandler;

    @Mock
    private GeoFenceExitEventHandler geoFenceExitEventHandler;

    @Mock
    private GeoFenceInsideEventHandler geoFenceInsideEventHandler;

    @Mock
    private GeoFenceOutsideEventHandler geoFenceOutsideEventHandler;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventService = new EventService(geoFenceRepository, eventRepository, strategyFactory,
                geoFenceEntryEventHandler, geoFenceExitEventHandler,
                geoFenceInsideEventHandler, geoFenceOutsideEventHandler);
    }

    @Test
    void testGetEventHistory() {
        List<List<GeoFenceEvent>> eventHistory = List.of(List.of(mock(GeoFenceEvent.class)));
        when(eventRepository.findAll()).thenReturn(eventHistory);

        List<List<GeoFenceEvent>> result = eventService.getEventHistory();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }


    @Test
    void testGetVehicleEventsHistory() {
        List<GeoFenceEvent> vehicleEvents = List.of(mock(GeoFenceEvent.class));
        when(eventRepository.findEventsVehicleId("V123")).thenReturn(vehicleEvents);

        List<GeoFenceEvent> result = eventService.getVehicleEventsHistory("V123");

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    @Test
    void testSaveJourneyEvent() {
        JourneyEvent journeyEvent = new JourneyEvent();
        journeyEvent.setVehicleId("V123");
        journeyEvent.setStartTime(LocalDateTime.now());

        eventService.saveJourneyEvent(journeyEvent);

        verify(eventRepository).saveJourneyEvent(journeyEvent);
    }

}
