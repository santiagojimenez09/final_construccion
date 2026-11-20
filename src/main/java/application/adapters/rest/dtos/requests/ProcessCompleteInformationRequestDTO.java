package application.adapters.rest.dtos.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessCompleteInformationRequestDTO {

    @JsonAlias({"satellites", "dispositivos"})
    @NotNull(message = "La lista de dispositivos es obligatoria.")
    @NotEmpty(message = "La lista de dispositivos no puede estar vacía.")
    @Size(min = 3, max = 3, message = "Se debe proporcionar exactamente la información de los 3 dispositivos de vigilancia.")
    @Valid
    private List<DeviceSignalRequestDTO> devices;
}
