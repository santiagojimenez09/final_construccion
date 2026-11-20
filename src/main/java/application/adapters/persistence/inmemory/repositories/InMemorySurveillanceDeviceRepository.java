package application.adapters.persistence.inmemory.repositories;

import application.adapters.persistence.inmemory.entities.SurveillanceDeviceMemoryEntity;
import application.domain.valueObjects.SurveillanceDeviceType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class InMemorySurveillanceDeviceRepository {

    private final Map<SurveillanceDeviceType, SurveillanceDeviceMemoryEntity> storage = new ConcurrentHashMap<>();

    public SurveillanceDeviceMemoryEntity save(SurveillanceDeviceMemoryEntity entity) {
        storage.put(entity.getDeviceType(), entity);
        return entity;
    }

    public Optional<SurveillanceDeviceMemoryEntity> findByDeviceType(SurveillanceDeviceType type) {
        return Optional.ofNullable(storage.get(type));
    }

    public List<SurveillanceDeviceMemoryEntity> findAll() {
        return new ArrayList<>(storage.values()).stream()
                .sorted(Comparator.comparingInt(e -> e.getDeviceType().ordinal()))
                .collect(Collectors.toList());
    }

    public boolean deleteByDeviceType(SurveillanceDeviceType type) {
        return storage.remove(type) != null;
    }

    public void deleteAll() {
        storage.clear();
    }
}
