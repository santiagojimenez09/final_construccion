package application.adapters.rest.dtos.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class ReplaceDeviceRequestDTO {

    private String name;

    @NotNull(message = "Para el reemplazo completo, la distancia actualizada es obligatoria.")
    @PositiveOrZero(message = "La distancia debe ser un número mayor o igual a cero.")
    private Double distance;

    @NotNull(message = "Para el reemplazo completo, el fragmento completo del mensaje es obligatorio.")
    @NotEmpty(message = "El fragmento del mensaje no puede estar vacío.")
    private List<String> message;
}
