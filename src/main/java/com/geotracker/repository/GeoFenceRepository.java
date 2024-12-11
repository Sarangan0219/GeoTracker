package com.geotracker.repository;

import com.geotracker.model.GeoFence;

import java.util.List;
import java.util.Optional;

public interface GeoFenceRepository {
    GeoFence save(GeoFence geoFence);

    Optional<GeoFence> findByName(String name);

    Optional<GeoFence> findById(String geoFenceid);

    List<GeoFence> findAll();

    void deleteByName(String name);
}
