package application.adapters.rest.dtos.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
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
public class DeviceSignalRequestDTO {

    @JsonAlias({"id", "identifier", "device", "deviceName"})
    private String name;

    @NotNull(message = "La distancia hasta el laboratorio es obligatoria.")
    @PositiveOrZero(message = "La distancia debe ser un número mayor o igual a cero.")
    private Double distance;

    @NotNull(message = "El fragmento del mensaje es obligatorio.")
    @NotEmpty(message = "El fragmento del mensaje no puede ser una lista vacía.")
    private List<String> message;
}
