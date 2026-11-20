package application.domain.services.surveillance;

import application.domain.exceptions.DomainException;
import application.domain.exceptions.UnknownDeviceException;
import application.domain.models.Position;
import application.domain.models.SurveillanceDevice;
import application.domain.ports.in.RegisterIndividualDeviceUseCase;
import application.domain.ports.out.SurveillanceDeviceRepositoryPort;
import application.domain.valueObjects.SurveillanceDeviceType;

import java.util.ArrayList;

public class RegisterIndividualDeviceService implements RegisterIndividualDeviceUseCase {

    private final SurveillanceDeviceRepositoryPort repositoryPort;

    public RegisterIndividualDeviceService(SurveillanceDeviceRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public SurveillanceDevice registerDevice(SurveillanceDevice device) {
        if (device == null) {
            throw new DomainException("La información del dispositivo no puede ser nula.");
        }

        SurveillanceDeviceType type = SurveillanceDeviceType.fromIdentifier(device.getIdentifier())
                .orElseThrow(() -> new UnknownDeviceException(
                        "Dispositivo de vigilancia desconocido: '" + device.getIdentifier()
                                + "'. Los dispositivos válidos son: STAR Labs, CCPD y S.T.A.R. Satellite."));

        if (device.getDistance() == null || Double.isNaN(device.getDistance()) || device.getDistance() < 0) {
            throw new DomainException("La distancia del dispositivo '" + type.getCanonicalName() + "' es obligatoria y debe ser mayor o igual a cero.");
        }

        if (device.getMessage() == null || device.getMessage().isEmpty()) {
            throw new DomainException("El fragmento del mensaje del dispositivo '" + type.getCanonicalName() + "' es obligatorio y no puede estar vacío.");
        }

        SurveillanceDevice normalized = new SurveillanceDevice(
                type.getCanonicalName(),
                type,
                new Position(type.getX(), type.getY()),
                device.getDistance(),
                new ArrayList<>(device.getMessage())
        );

        return repositoryPort.save(normalized);
    }
}
