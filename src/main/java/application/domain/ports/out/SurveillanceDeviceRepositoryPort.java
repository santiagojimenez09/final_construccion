package application.domain.ports.out;

import application.domain.models.SurveillanceDevice;
import application.domain.valueObjects.SurveillanceDeviceType;

import java.util.List;
import java.util.Optional;

public interface SurveillanceDeviceRepositoryPort {
    SurveillanceDevice save(SurveillanceDevice device);
    Optional<SurveillanceDevice> findByDeviceType(SurveillanceDeviceType deviceType);
    List<SurveillanceDevice> findAll();
    boolean deleteByDeviceType(SurveillanceDeviceType deviceType);
    void deleteAll();
}
