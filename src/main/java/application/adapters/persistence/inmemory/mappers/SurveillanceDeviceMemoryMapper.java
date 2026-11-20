package application.adapters.persistence.inmemory.mappers;

import application.adapters.persistence.inmemory.entities.SurveillanceDeviceMemoryEntity;
import application.domain.models.Position;
import application.domain.models.SurveillanceDevice;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SurveillanceDeviceMemoryMapper {

    private SurveillanceDeviceMemoryMapper() {
    }

    public static SurveillanceDeviceMemoryEntity toEntity(SurveillanceDevice domain) {
        if (domain == null) {
            return null;
        }
        double x = domain.getFixedPosition() != null ? domain.getFixedPosition().getX() : domain.getDeviceType().getX();
        double y = domain.getFixedPosition() != null ? domain.getFixedPosition().getY() : domain.getDeviceType().getY();
        return SurveillanceDeviceMemoryEntity.builder()
                .identifier(domain.getIdentifier())
                .deviceType(domain.getDeviceType())
                .posX(x)
                .posY(y)
                .distance(domain.getDistance())
                .message(domain.getMessage() != null ? new ArrayList<>(domain.getMessage()) : new ArrayList<>())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static SurveillanceDevice toDomain(SurveillanceDeviceMemoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return new SurveillanceDevice(
                entity.getIdentifier(),
                entity.getDeviceType(),
                new Position(entity.getPosX(), entity.getPosY()),
                entity.getDistance(),
                entity.getMessage() != null ? new ArrayList<>(entity.getMessage()) : new ArrayList<>()
        );
    }
}
