package application.domain.ports.in;

import application.domain.models.SurveillanceDevice;

public interface PartialUpdateDeviceInformationUseCase {
    SurveillanceDevice partialUpdateDeviceInformation(String identifier, SurveillanceDevice partialInformation);
}
