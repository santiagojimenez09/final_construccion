package application.domain.ports.in;

import application.domain.models.SurveillanceDevice;

public interface ReplaceDeviceInformationUseCase {
    SurveillanceDevice replaceDeviceInformation(String identifier, SurveillanceDevice newInformation);
}
