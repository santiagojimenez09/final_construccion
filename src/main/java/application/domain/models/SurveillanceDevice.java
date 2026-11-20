package application.domain.models;

import application.domain.valueObjects.SurveillanceDeviceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveillanceDevice {
    private String identifier;
    private SurveillanceDeviceType deviceType;
    private Position fixedPosition;
    private Double distance;
    private List<String> message;
}
