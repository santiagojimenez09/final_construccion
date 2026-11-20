package application.domain.services.surveillance;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.InsufficientInformationException;
import application.domain.exceptions.UnknownDeviceException;
import application.domain.models.LaboratoryIntelligence;
import application.domain.models.Position;
import application.domain.models.SurveillanceDevice;
import application.domain.ports.in.ProcessCompleteInformationUseCase;
import application.domain.ports.out.SurveillanceDeviceRepositoryPort;
import application.domain.services.reconstruction.MessageReconstructionService;
import application.domain.services.trilateration.TrilaterationService;
import application.domain.valueObjects.SurveillanceDeviceType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ProcessCompleteInformationService implements ProcessCompleteInformationUseCase {

    private final TrilaterationService trilaterationService;
    private final MessageReconstructionService messageReconstructionService;
    private final SurveillanceDeviceRepositoryPort repositoryPort;

    public ProcessCompleteInformationService(TrilaterationService trilaterationService,
                                             MessageReconstructionService messageReconstructionService,
                                             SurveillanceDeviceRepositoryPort repositoryPort) {
        this.trilaterationService = trilaterationService;
        this.messageReconstructionService = messageReconstructionService;
        this.repositoryPort = repositoryPort;
    }

    @Override
    public LaboratoryIntelligence processCompleteInformation(List<SurveillanceDevice> devices) {
        if (devices == null || devices.size() != 3) {
            throw new InsufficientInformationException(
                    "Se debe proporcionar exactamente la información de los 3 dispositivos de vigilancia (STAR Labs, CCPD y S.T.A.R. Satellite).");
        }

        Map<SurveillanceDeviceType, SurveillanceDevice> validatedByType = new EnumMap<>(SurveillanceDeviceType.class);

        for (SurveillanceDevice incoming : devices) {
            if (incoming == null) {
                throw new DomainException("Los datos del dispositivo de vigilancia no pueden ser nulos.");
            }
            SurveillanceDeviceType type = SurveillanceDeviceType.fromIdentifier(incoming.getIdentifier())
                    .orElseThrow(() -> new UnknownDeviceException(
                            "Dispositivo de vigilancia desconocido: '" + incoming.getIdentifier()
                                    + "'. Los dispositivos válidos son: STAR Labs, CCPD y S.T.A.R. Satellite."));

            if (validatedByType.containsKey(type)) {
                throw new DomainException("El dispositivo '" + type.getCanonicalName() + "' fue enviado más de una vez en la misma solicitud.");
            }

            if (incoming.getDistance() == null || Double.isNaN(incoming.getDistance()) || incoming.getDistance() < 0) {
                throw new DomainException("La distancia del dispositivo '" + type.getCanonicalName() + "' debe ser un número mayor o igual a cero.");
            }

            if (incoming.getMessage() == null || incoming.getMessage().isEmpty()) {
                throw new DomainException("El fragmento de mensaje del dispositivo '" + type.getCanonicalName() + "' no puede ser nulo ni vacío.");
            }

            SurveillanceDevice normalized = new SurveillanceDevice(
                    type.getCanonicalName(),
                    type,
                    new Position(type.getX(), type.getY()),
                    incoming.getDistance(),
                    new ArrayList<>(incoming.getMessage())
            );
            validatedByType.put(type, normalized);
        }

        List<SurveillanceDevice> orderedDevices = new ArrayList<>(validatedByType.values());

        Position position = trilaterationService.getLocation(orderedDevices);
        String message = messageReconstructionService.getMessage(orderedDevices);

        // Save in memory as well so state reflects the latest received information
        for (SurveillanceDevice device : orderedDevices) {
            repositoryPort.save(device);
        }

        return new LaboratoryIntelligence(position, message);
    }
}
