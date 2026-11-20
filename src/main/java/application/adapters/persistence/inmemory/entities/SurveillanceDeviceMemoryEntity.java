package application.adapters.persistence.inmemory.entities;

import application.domain.valueObjects.SurveillanceDeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveillanceDeviceMemoryEntity {
    private String identifier;
    private SurveillanceDeviceType deviceType;
    private double posX;
    private double posY;
    private Double distance;
    private List<String> message;
    private LocalDateTime updatedAt;
}
