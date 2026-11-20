package application.domain.ports.in;

import application.domain.models.LaboratoryIntelligence;
import application.domain.models.SurveillanceDevice;

import java.util.List;

public interface ConsultAccumulatedInformationUseCase {
    LaboratoryIntelligence processAccumulatedInformation();
    List<SurveillanceDevice> consultRegisteredDevices();
    SurveillanceDevice consultDeviceByIdentifier(String identifier);
}
