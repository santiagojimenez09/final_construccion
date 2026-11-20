package application.adapters.rest.mappers;

import application.adapters.rest.dtos.requests.DeviceSignalRequestDTO;
import application.adapters.rest.dtos.requests.PartialUpdateDeviceRequestDTO;
import application.adapters.rest.dtos.requests.ProcessCompleteInformationRequestDTO;
import application.adapters.rest.dtos.requests.ReplaceDeviceRequestDTO;
import application.adapters.rest.dtos.responses.DeviceSignalResponseDTO;
import application.adapters.rest.dtos.responses.LaboratoryIntelligenceResponseDTO;
import application.adapters.rest.dtos.responses.PositionResponseDTO;
import application.domain.models.LaboratoryIntelligence;
import application.domain.models.Position;
import application.domain.models.SurveillanceDevice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SurveillanceRestMapper {

    private SurveillanceRestMapper() {
    }

    public static SurveillanceDevice toDomain(DeviceSignalRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        SurveillanceDevice device = new SurveillanceDevice();
        device.setIdentifier(dto.getName());
        device.setDistance(dto.getDistance());
        device.setMessage(dto.getMessage());
        return device;
    }

    public static SurveillanceDevice toDomain(String identifier, DeviceSignalRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        SurveillanceDevice device = new SurveillanceDevice();
        device.setIdentifier(identifier != null && !identifier.isBlank() ? identifier : dto.getName());
        device.setDistance(dto.getDistance());
        device.setMessage(dto.getMessage());
        return device;
    }

    public static List<SurveillanceDevice> toDomainList(ProcessCompleteInformationRequestDTO dto) {
        if (dto == null || dto.getDevices() == null) {
            return Collections.emptyList();
        }
        return dto.getDevices().stream()
                .map(SurveillanceRestMapper::toDomain)
                .collect(Collectors.toList());
    }

    public static SurveillanceDevice toDomain(ReplaceDeviceRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        SurveillanceDevice device = new SurveillanceDevice();
        device.setIdentifier(dto.getName());
        device.setDistance(dto.getDistance());
        device.setMessage(dto.getMessage());
        return device;
    }

    public static SurveillanceDevice toDomain(PartialUpdateDeviceRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        SurveillanceDevice device = new SurveillanceDevice();
        device.setDistance(dto.getDistance());
        device.setMessage(dto.getMessage());
        return device;
    }

    public static PositionResponseDTO toPositionResponseDTO(Position position) {
        if (position == null) {
            return null;
        }
        return PositionResponseDTO.builder()
                .x(position.getX())
                .y(position.getY())
                .build();
    }

    public static LaboratoryIntelligenceResponseDTO toIntelligenceResponseDTO(LaboratoryIntelligence intelligence) {
        if (intelligence == null) {
            return null;
        }
        return LaboratoryIntelligenceResponseDTO.builder()
                .position(toPositionResponseDTO(intelligence.getPosition()))
                .message(intelligence.getMessage())
                .build();
    }

    public static DeviceSignalResponseDTO toDeviceResponseDTO(SurveillanceDevice device) {
        if (device == null) {
            return null;
        }
        return DeviceSignalResponseDTO.builder()
                .name(device.getIdentifier())
                .fixedPosition(toPositionResponseDTO(device.getFixedPosition()))
                .distance(device.getDistance())
                .message(device.getMessage())
                .build();
    }
}
