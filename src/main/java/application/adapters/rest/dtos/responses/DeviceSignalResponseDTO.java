package application.adapters.rest.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSignalResponseDTO {
    private String name;
    private PositionResponseDTO fixedPosition;
    private Double distance;
    private List<String> message;
}
