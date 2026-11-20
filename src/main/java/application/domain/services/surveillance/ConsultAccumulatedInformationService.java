package application.domain.services.surveillance;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.InsufficientInformationException;
import application.domain.exceptions.UnknownDeviceException;
import application.domain.models.LaboratoryIntelligence;
import application.domain.models.Position;
import application.domain.models.SurveillanceDevice;
import application.domain.ports.in.ConsultAccumulatedInformationUseCase;
import application.domain.ports.out.SurveillanceDeviceRepositoryPort;
import application.domain.services.reconstruction.MessageReconstructionService;
import application.domain.services.trilateration.TrilaterationService;
import application.domain.valueObjects.SurveillanceDeviceType;

import java.util.List;

public class ConsultAccumulatedInformationService implements ConsultAccumulatedInformationUseCase {

    private final SurveillanceDeviceRepositoryPort repositoryPort;
    private final TrilaterationService trilaterationService;
    private final MessageReconstructionService messageReconstructionService;

    public ConsultAccumulatedInformationService(SurveillanceDeviceRepositoryPort repositoryPort,
                                                TrilaterationService trilaterationService,
                                                MessageReconstructionService messageReconstructionService) {
        this.repositoryPort = repositoryPort;
        this.trilaterationService = trilaterationService;
        this.messageReconstructionService = messageReconstructionService;
    }

    @Override
    public LaboratoryIntelligence processAccumulatedInformation() {
        List<SurveillanceDevice> registeredDevices = repositoryPort.findAll();
        if (registeredDevices == null || registeredDevices.size() < 3) {
            int count = registeredDevices == null ? 0 : registeredDevices.size();
            throw new InsufficientInformationException(
                    "No existen datos suficientes para determinar la ubicación y reconstruir el mensaje. Dispositivos registrados actualmente: "
                            + count + " de 3.");
        }

        Position position = trilaterationService.getLocation(registeredDevices);
        String message = messageReconstructionService.getMessage(registeredDevices);

        return new LaboratoryIntelligence(position, message);
    }

    @Override
    public List<SurveillanceDevice> consultRegisteredDevices() {
        return repositoryPort.findAll();
    }

    @Override
    public SurveillanceDevice consultDeviceByIdentifier(String identifier) {
        SurveillanceDeviceType type = SurveillanceDeviceType.fromIdentifier(identifier)
                .orElseThrow(() -> new UnknownDeviceException(
                        "Dispositivo de vigilancia desconocido: '" + identifier
                                + "'. Los dispositivos válidos son: STAR Labs, CCPD y S.T.A.R. Satellite."));

        return repositoryPort.findByDeviceType(type)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe información registrada en memoria para el dispositivo '" + type.getCanonicalName() + "'."));
    }
}
