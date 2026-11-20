package application.domain.services.surveillance;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnknownDeviceException;
import application.domain.models.Position;
import application.domain.models.SurveillanceDevice;
import application.domain.ports.in.PartialUpdateDeviceInformationUseCase;
import application.domain.ports.out.SurveillanceDeviceRepositoryPort;
import application.domain.valueObjects.SurveillanceDeviceType;

import java.util.ArrayList;
import java.util.List;

public class PartialUpdateDeviceInformationService implements PartialUpdateDeviceInformationUseCase {

    private final SurveillanceDeviceRepositoryPort repositoryPort;

    public PartialUpdateDeviceInformationService(SurveillanceDeviceRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public SurveillanceDevice partialUpdateDeviceInformation(String identifier, SurveillanceDevice partialInformation) {
        SurveillanceDeviceType type = SurveillanceDeviceType.fromIdentifier(identifier)
                .orElseThrow(() -> new UnknownDeviceException(
                        "Dispositivo de vigilancia desconocido: '" + identifier
                                + "'. Los dispositivos válidos son: STAR Labs, CCPD y S.T.A.R. Satellite."));

        if (partialInformation == null) {
            throw new DomainException("Los datos para la actualización parcial no pueden ser nulos.");
        }

        SurveillanceDevice existing = repositoryPort.findByDeviceType(type)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No se puede actualizar parcialmente: el dispositivo '" + type.getCanonicalName()
                                + "' no tiene información previamente registrada."));

        boolean hasDistance = partialInformation.getDistance() != null;
        boolean hasMessage = partialInformation.getMessage() != null;

        if (!hasDistance && !hasMessage) {
            throw new DomainException(
                    "Para la actualización parcial debe proporcionar al menos la distancia ('distance') o el fragmento del mensaje ('message').");
        }

        Double updatedDistance = existing.getDistance();
        if (hasDistance) {
            if (Double.isNaN(partialInformation.getDistance()) || partialInformation.getDistance() < 0) {
                throw new DomainException("La distancia proporcionada debe ser un número mayor o igual a cero.");
            }
            updatedDistance = partialInformation.getDistance();
        }

        List<String> updatedMessage = existing.getMessage();
        if (hasMessage) {
            if (partialInformation.getMessage().isEmpty()) {
                throw new DomainException("El fragmento de mensaje proporcionado no puede ser una lista vacía.");
            }
            updatedMessage = new ArrayList<>(partialInformation.getMessage());
        }

        SurveillanceDevice updated = new SurveillanceDevice(
                type.getCanonicalName(),
                type,
                new Position(type.getX(), type.getY()),
                updatedDistance,
                updatedMessage
        );

        return repositoryPort.save(updated);
    }
}
