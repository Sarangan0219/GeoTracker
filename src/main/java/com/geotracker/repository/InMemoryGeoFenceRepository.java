package com.geotracker.repository;

import com.geotracker.model.GeoFence;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("In-memory")
public class InMemoryGeoFenceRepository implements GeoFenceRepository {

    private final Map<String, GeoFence> geoFences = new ConcurrentHashMap<>();

    @Override
    public GeoFence save(GeoFence geoFence) {
        geoFences.put(geoFence.getGeoFenceId(), geoFence);
        return geoFence;
    }

    @Override
    public Optional<GeoFence> findByName(String name) {
        return geoFences.values().stream()
                .filter(geoFence -> geoFence.getName().equals(name))
                .findFirst();
    }

    @Override
    public Optional<GeoFence> findById(String geoFenceId) {
        return Optional.ofNullable(geoFences.get(geoFenceId));
    }

    @Override
    public List<GeoFence> findAll() {
        return new ArrayList<>(geoFences.values());
    }

    @Override
    public void deleteByName(String name) {
        geoFences.remove(name);
    }
}
