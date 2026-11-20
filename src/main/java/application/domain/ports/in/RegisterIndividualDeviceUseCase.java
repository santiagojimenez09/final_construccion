package application.domain.ports.in;

import application.domain.models.SurveillanceDevice;

public interface RegisterIndividualDeviceUseCase {
    SurveillanceDevice registerDevice(SurveillanceDevice device);
}
