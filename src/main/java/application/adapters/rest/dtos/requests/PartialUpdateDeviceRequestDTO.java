package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartialUpdateDeviceRequestDTO {

    @PositiveOrZero(message = "La distancia debe ser un número mayor o igual a cero.")
    private Double distance;

    private List<String> message;
}
