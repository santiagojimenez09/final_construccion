package application.domain.services.surveillance;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnknownDeviceException;
import application.domain.models.Position;
import application.domain.models.SurveillanceDevice;
import application.domain.ports.in.ReplaceDeviceInformationUseCase;
import application.domain.ports.out.SurveillanceDeviceRepositoryPort;
import application.domain.valueObjects.SurveillanceDeviceType;

import java.util.ArrayList;

public class ReplaceDeviceInformationService implements ReplaceDeviceInformationUseCase {

    private final SurveillanceDeviceRepositoryPort repositoryPort;

    public ReplaceDeviceInformationService(SurveillanceDeviceRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public SurveillanceDevice replaceDeviceInformation(String identifier, SurveillanceDevice newInformation) {
        SurveillanceDeviceType type = SurveillanceDeviceType.fromIdentifier(identifier)
                .orElseThrow(() -> new UnknownDeviceException(
                        "Dispositivo de vigilancia desconocido: '" + identifier
                                + "'. Los dispositivos válidos son: STAR Labs, CCPD y S.T.A.R. Satellite."));

        if (newInformation == null) {
            throw new DomainException("La nueva información para reemplazar el dispositivo no puede ser nula.");
        }

        if (newInformation.getIdentifier() != null && !newInformation.getIdentifier().isBlank()) {
            SurveillanceDeviceType bodyType = SurveillanceDeviceType.fromIdentifier(newInformation.getIdentifier())
                    .orElseThrow(() -> new UnknownDeviceException(
                            "Dispositivo desconocido en el cuerpo de la solicitud: '" + newInformation.getIdentifier() + "'."));
            if (bodyType != type) {
                throw new DomainException("El identificador del dispositivo en la ruta ('" + type.getCanonicalName()
                        + "') no coincide con el del cuerpo ('" + bodyType.getCanonicalName() + "').");
            }
        }

        repositoryPort.findByDeviceType(type)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se puede reemplazar la información: el dispositivo '" + type.getCanonicalName()
                                + "' no cuenta con información previamente registrada."));

        if (newInformation.getDistance() == null || Double.isNaN(newInformation.getDistance()) || newInformation.getDistance() < 0) {
            throw new DomainException("Para el reemplazo completo, la distancia actualizada es obligatoria y debe ser mayor o igual a cero.");
        }

        if (newInformation.getMessage() == null || newInformation.getMessage().isEmpty()) {
            throw new DomainException("Para el reemplazo completo, el fragmento completo del mensaje es obligatorio y no puede estar vacío.");
        }

        SurveillanceDevice replacement = new SurveillanceDevice(
                type.getCanonicalName(),
                type,
                new Position(type.getX(), type.getY()),
                newInformation.getDistance(),
                new ArrayList<>(newInformation.getMessage())
        );

        return repositoryPort.save(replacement);
    }
}
