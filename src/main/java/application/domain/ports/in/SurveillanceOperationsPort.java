package application.domain.ports.in;

import application.domain.models.LaboratoryIntelligence;
import application.domain.models.SurveillanceDevice;

import java.util.List;

public interface SurveillanceOperationsPort {
    // 2.1 Procesamiento de informacion completa (F1)
    LaboratoryIntelligence processCompleteInformation(List<SurveillanceDevice> devices);

    // 2.2 Registro de informacion individual (F2)
    SurveillanceDevice registerDeviceInformation(SurveillanceDevice device);

    // 2.3 Consulta y procesamiento de informacion acumulada (F3)
    LaboratoryIntelligence consultAndProcessAccumulatedInformation();
    List<SurveillanceDevice> consultRegisteredDevices();
    SurveillanceDevice consultDeviceInformation(String identifier);

    // 2.4 Reemplazo de informacion de un dispositivo (F4)
    SurveillanceDevice replaceDeviceInformation(String identifier, SurveillanceDevice device);

    // 2.5 Actualizacion parcial de informacion de un dispositivo (F5)
    SurveillanceDevice partialUpdateDeviceInformation(String identifier, SurveillanceDevice partialDevice);

    // 2.6 Eliminacion de informacion de un dispositivo (F6)
    void deleteDeviceInformation(String identifier);
}
