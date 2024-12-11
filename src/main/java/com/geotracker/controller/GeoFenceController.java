package com.geotracker.controller;

import com.geotracker.model.request.GeoFenceRequest;
import com.geotracker.model.view.GeoFenceView;
import com.geotracker.service.GeoFenceService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/geofences")
@Slf4j
public class GeoFenceController {

    private final GeoFenceService geoFenceService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ApiResponse(responseCode = "201", description = "GeoFence created successfully")
    public ResponseEntity<GeoFenceView> createGeoFence(@RequestBody @Valid GeoFenceRequest geoFenceRequest) {
        GeoFenceView createdGeoFence = geoFenceService.createGeoFence(geoFenceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGeoFence);
    }

    @GetMapping
    public ResponseEntity<List<GeoFenceView>> getGeoFences(@RequestParam(required = false) String name) {
        if (name != null) {
            GeoFenceView geoFence = geoFenceService.getGeoFenceByName(name);
            return geoFence != null
                    ? ResponseEntity.ok(List.of(geoFence))
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<GeoFenceView> geoFences = geoFenceService.getAllGeoFencesForView();
        return geoFences.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.ok(geoFences);
    }

    @GetMapping("/{geoFenceId}")
    public ResponseEntity<GeoFenceView> getGeoFenceById(@PathVariable String geoFenceId) {
        GeoFenceView geoFence = geoFenceService.getGeoFenceById(geoFenceId);
        return geoFence != null
                ? ResponseEntity.ok(geoFence)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/{name}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<GeoFenceView> updateGeoFence(
            @PathVariable String name, @RequestBody @Valid GeoFenceRequest updatedGeoFence) {
        GeoFenceView updatedGeoFenceView = geoFenceService.updateGeoFence(name, updatedGeoFence);
        return ResponseEntity.ok(updatedGeoFenceView);
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteGeoFence(@PathVariable String name) {
        geoFenceService.deleteGeoFence(name);
        return ResponseEntity.noContent().build();
    }
}
