package application.adapters.rest.controllers;

import application.adapters.rest.dtos.requests.DeviceSignalRequestDTO;
import application.adapters.rest.dtos.requests.PartialUpdateDeviceRequestDTO;
import application.adapters.rest.dtos.requests.ProcessCompleteInformationRequestDTO;
import application.adapters.rest.dtos.requests.ReplaceDeviceRequestDTO;
import application.adapters.rest.dtos.responses.DeviceSignalResponseDTO;
import application.adapters.rest.dtos.responses.LaboratoryIntelligenceResponseDTO;
import application.adapters.rest.mappers.SurveillanceRestMapper;
import application.domain.models.LaboratoryIntelligence;
import application.domain.models.SurveillanceDevice;
import application.domain.ports.in.SurveillanceOperationsPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class SurveillanceController {

    private final SurveillanceOperationsPort surveillanceOperationsPort;

    // 2.1. Procesamiento de información completa (F1)
    @PostMapping({"/api/v1/surveillance", "/api/v1/surveillance/process", "/topsecret"})
    public ResponseEntity<LaboratoryIntelligenceResponseDTO> processCompleteInformation(
            @Valid @RequestBody ProcessCompleteInformationRequestDTO requestDTO) {
        List<SurveillanceDevice> devices = SurveillanceRestMapper.toDomainList(requestDTO);
        LaboratoryIntelligence intelligence = surveillanceOperationsPort.processCompleteInformation(devices);
        return ResponseEntity.ok(SurveillanceRestMapper.toIntelligenceResponseDTO(intelligence));
    }

    // 2.2. Registro de información individual - Identificación en el cuerpo (F2)
    @PostMapping({"/api/v1/surveillance/devices", "/topsecret_split"})
    public ResponseEntity<DeviceSignalResponseDTO> registerDeviceFromBody(
            @Valid @RequestBody DeviceSignalRequestDTO requestDTO) {
        SurveillanceDevice device = SurveillanceRestMapper.toDomain(requestDTO);
        SurveillanceDevice saved = surveillanceOperationsPort.registerDeviceInformation(device);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SurveillanceRestMapper.toDeviceResponseDTO(saved));
    }

    // 2.2. Registro de información individual - Identificación en la URL (F2)
    @PostMapping({"/api/v1/surveillance/devices/{deviceId}", "/topsecret_split/{deviceId}"})
    public ResponseEntity<DeviceSignalResponseDTO> registerDeviceByPath(
            @PathVariable("deviceId") String deviceId,
            @Valid @RequestBody DeviceSignalRequestDTO requestDTO) {
        SurveillanceDevice device = SurveillanceRestMapper.toDomain(deviceId, requestDTO);
        SurveillanceDevice saved = surveillanceOperationsPort.registerDeviceInformation(device);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SurveillanceRestMapper.toDeviceResponseDTO(saved));
    }

    // 2.3. Consulta y procesamiento de información acumulada (F3)
    @GetMapping({"/api/v1/surveillance", "/api/v1/surveillance/devices/process", "/topsecret_split"})
    public ResponseEntity<LaboratoryIntelligenceResponseDTO> consultAndProcessAccumulatedInformation() {
        LaboratoryIntelligence intelligence = surveillanceOperationsPort.consultAndProcessAccumulatedInformation();
        return ResponseEntity.ok(SurveillanceRestMapper.toIntelligenceResponseDTO(intelligence));
    }

    // 2.3. Consulta del listado de dispositivos actualmente registrados en memoria
    @GetMapping("/api/v1/surveillance/devices")
    public ResponseEntity<List<DeviceSignalResponseDTO>> consultRegisteredDevices() {
        List<SurveillanceDevice> devices = surveillanceOperationsPort.consultRegisteredDevices();
        List<DeviceSignalResponseDTO> response = devices.stream()
                .map(SurveillanceRestMapper::toDeviceResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 2.3. Consulta de un dispositivo individual registrado
    @GetMapping({"/api/v1/surveillance/devices/{deviceId}", "/topsecret_split/{deviceId}"})
    public ResponseEntity<DeviceSignalResponseDTO> consultDeviceByIdentifier(
            @PathVariable("deviceId") String deviceId) {
        SurveillanceDevice device = surveillanceOperationsPort.consultDeviceInformation(deviceId);
        return ResponseEntity.ok(SurveillanceRestMapper.toDeviceResponseDTO(device));
    }

    // 2.4. Reemplazo completo de información de un dispositivo (F4)
    @PutMapping({"/api/v1/surveillance/devices/{deviceId}", "/topsecret_split/{deviceId}"})
    public ResponseEntity<DeviceSignalResponseDTO> replaceDeviceInformation(
            @PathVariable("deviceId") String deviceId,
            @Valid @RequestBody ReplaceDeviceRequestDTO requestDTO) {
        SurveillanceDevice replacement = SurveillanceRestMapper.toDomain(requestDTO);
        SurveillanceDevice updated = surveillanceOperationsPort.replaceDeviceInformation(deviceId, replacement);
        return ResponseEntity.ok(SurveillanceRestMapper.toDeviceResponseDTO(updated));
    }

    // 2.5. Actualización parcial de información de un dispositivo (F5)
    @PatchMapping({"/api/v1/surveillance/devices/{deviceId}", "/topsecret_split/{deviceId}"})
    public ResponseEntity<DeviceSignalResponseDTO> partialUpdateDeviceInformation(
            @PathVariable("deviceId") String deviceId,
            @Valid @RequestBody PartialUpdateDeviceRequestDTO requestDTO) {
        SurveillanceDevice partial = SurveillanceRestMapper.toDomain(requestDTO);
        SurveillanceDevice updated = surveillanceOperationsPort.partialUpdateDeviceInformation(deviceId, partial);
        return ResponseEntity.ok(SurveillanceRestMapper.toDeviceResponseDTO(updated));
    }

    // 2.6. Eliminación de información de un dispositivo (F6)
    @DeleteMapping({"/api/v1/surveillance/devices/{deviceId}", "/topsecret_split/{deviceId}"})
    public ResponseEntity<Void> deleteDeviceInformation(@PathVariable("deviceId") String deviceId) {
        surveillanceOperationsPort.deleteDeviceInformation(deviceId);
        return ResponseEntity.noContent().build();
    }
}
