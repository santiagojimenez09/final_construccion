package application.adapters.useCases;

import application.domain.models.LaboratoryIntelligence;
import application.domain.models.SurveillanceDevice;
import application.domain.ports.in.SurveillanceOperationsPort;
import application.domain.services.surveillance.ConsultAccumulatedInformationService;
import application.domain.services.surveillance.DeleteDeviceInformationService;
import application.domain.services.surveillance.PartialUpdateDeviceInformationService;
import application.domain.services.surveillance.ProcessCompleteInformationService;
import application.domain.services.surveillance.RegisterIndividualDeviceService;
import application.domain.services.surveillance.ReplaceDeviceInformationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveillanceOperationsUseCaseImpl implements SurveillanceOperationsPort {

    private final ProcessCompleteInformationService processCompleteInformationService;
    private final RegisterIndividualDeviceService registerIndividualDeviceService;
    private final ConsultAccumulatedInformationService consultAccumulatedInformationService;
    private final ReplaceDeviceInformationService replaceDeviceInformationService;
    private final PartialUpdateDeviceInformationService partialUpdateDeviceInformationService;
    private final DeleteDeviceInformationService deleteDeviceInformationService;

    @Override
    public LaboratoryIntelligence processCompleteInformation(List<SurveillanceDevice> devices) {
        return processCompleteInformationService.processCompleteInformation(devices);
    }

    @Override
    public SurveillanceDevice registerDeviceInformation(SurveillanceDevice device) {
        return registerIndividualDeviceService.registerDevice(device);
    }

    @Override
    public LaboratoryIntelligence consultAndProcessAccumulatedInformation() {
        return consultAccumulatedInformationService.processAccumulatedInformation();
    }

    @Override
    public List<SurveillanceDevice> consultRegisteredDevices() {
        return consultAccumulatedInformationService.consultRegisteredDevices();
    }

    @Override
    public SurveillanceDevice consultDeviceInformation(String identifier) {
        return consultAccumulatedInformationService.consultDeviceByIdentifier(identifier);
    }

    @Override
    public SurveillanceDevice replaceDeviceInformation(String identifier, SurveillanceDevice device) {
        return replaceDeviceInformationService.replaceDeviceInformation(identifier, device);
    }

    @Override
    public SurveillanceDevice partialUpdateDeviceInformation(String identifier, SurveillanceDevice partialDevice) {
        return partialUpdateDeviceInformationService.partialUpdateDeviceInformation(identifier, partialDevice);
    }

    @Override
    public void deleteDeviceInformation(String identifier) {
        deleteDeviceInformationService.deleteDeviceInformation(identifier);
    }
}
