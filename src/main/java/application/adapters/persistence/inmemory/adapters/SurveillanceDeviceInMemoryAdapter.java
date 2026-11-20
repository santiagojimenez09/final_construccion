package application.adapters.persistence.inmemory.adapters;

import application.adapters.persistence.inmemory.entities.SurveillanceDeviceMemoryEntity;
import application.adapters.persistence.inmemory.mappers.SurveillanceDeviceMemoryMapper;
import application.adapters.persistence.inmemory.repositories.InMemorySurveillanceDeviceRepository;
import application.domain.models.SurveillanceDevice;
import application.domain.ports.out.SurveillanceDeviceRepositoryPort;
import application.domain.valueObjects.SurveillanceDeviceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SurveillanceDeviceInMemoryAdapter implements SurveillanceDeviceRepositoryPort {

    private final InMemorySurveillanceDeviceRepository repository;

    @Override
    public SurveillanceDevice save(SurveillanceDevice device) {
        if (device == null) {
            return null;
        }
        SurveillanceDeviceMemoryEntity entity = SurveillanceDeviceMemoryMapper.toEntity(device);
        SurveillanceDeviceMemoryEntity saved = repository.save(entity);
        return SurveillanceDeviceMemoryMapper.toDomain(saved);
    }

    @Override
    public Optional<SurveillanceDevice> findByDeviceType(SurveillanceDeviceType deviceType) {
        if (deviceType == null) {
            return Optional.empty();
        }
        return repository.findByDeviceType(deviceType)
                .map(SurveillanceDeviceMemoryMapper::toDomain);
    }

    @Override
    public List<SurveillanceDevice> findAll() {
        return repository.findAll().stream()
                .map(SurveillanceDeviceMemoryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteByDeviceType(SurveillanceDeviceType deviceType) {
        if (deviceType == null) {
            return false;
        }
        return repository.deleteByDeviceType(deviceType);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
