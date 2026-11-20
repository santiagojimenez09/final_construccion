package application.domain.services.surveillance;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnknownDeviceException;
import application.domain.ports.in.DeleteDeviceInformationUseCase;
import application.domain.ports.out.SurveillanceDeviceRepositoryPort;
import application.domain.valueObjects.SurveillanceDeviceType;

public class DeleteDeviceInformationService implements DeleteDeviceInformationUseCase {

    private final SurveillanceDeviceRepositoryPort repositoryPort;

    public DeleteDeviceInformationService(SurveillanceDeviceRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void deleteDeviceInformation(String identifier) {
        SurveillanceDeviceType type = SurveillanceDeviceType.fromIdentifier(identifier)
                .orElseThrow(() -> new UnknownDeviceException(
                        "Dispositivo de vigilancia desconocido: '" + identifier
                                + "'. Los dispositivos válidos son: STAR Labs, CCPD y S.T.A.R. Satellite."));

        boolean removed = repositoryPort.deleteByDeviceType(type);
        if (!removed) {
            throw new EntityNotFoundException(
                    "No se puede eliminar la información: el dispositivo '" + type.getCanonicalName()
                            + "' no tiene información temporal almacenada.");
        }
    }
}
