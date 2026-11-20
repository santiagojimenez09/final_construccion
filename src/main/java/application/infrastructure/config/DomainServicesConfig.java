package application.infrastructure.config;

import application.domain.ports.out.SurveillanceDeviceRepositoryPort;
import application.domain.services.reconstruction.MessageReconstructionService;
import application.domain.services.surveillance.ConsultAccumulatedInformationService;
import application.domain.services.surveillance.DeleteDeviceInformationService;
import application.domain.services.surveillance.PartialUpdateDeviceInformationService;
import application.domain.services.surveillance.ProcessCompleteInformationService;
import application.domain.services.surveillance.RegisterIndividualDeviceService;
import application.domain.services.surveillance.ReplaceDeviceInformationService;
import application.domain.services.trilateration.TrilaterationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServicesConfig {

    // --- Trilateration & Message Reconstruction Subdomains ---
    @Bean
    public TrilaterationService trilaterationService() {
        return new TrilaterationService();
    }

    @Bean
    public MessageReconstructionService messageReconstructionService() {
        return new MessageReconstructionService();
    }

    // --- Surveillance Operations Subdomain ---
    @Bean
    public ProcessCompleteInformationService processCompleteInformationService(
            TrilaterationService trilaterationService,
            MessageReconstructionService messageReconstructionService,
            SurveillanceDeviceRepositoryPort repositoryPort) {
        return new ProcessCompleteInformationService(trilaterationService, messageReconstructionService, repositoryPort);
    }

    @Bean
    public RegisterIndividualDeviceService registerIndividualDeviceService(
            SurveillanceDeviceRepositoryPort repositoryPort) {
        return new RegisterIndividualDeviceService(repositoryPort);
    }

    @Bean
    public ConsultAccumulatedInformationService consultAccumulatedInformationService(
            SurveillanceDeviceRepositoryPort repositoryPort,
            TrilaterationService trilaterationService,
            MessageReconstructionService messageReconstructionService) {
        return new ConsultAccumulatedInformationService(repositoryPort, trilaterationService, messageReconstructionService);
    }

    @Bean
    public ReplaceDeviceInformationService replaceDeviceInformationService(
            SurveillanceDeviceRepositoryPort repositoryPort) {
        return new ReplaceDeviceInformationService(repositoryPort);
    }

    @Bean
    public PartialUpdateDeviceInformationService partialUpdateDeviceInformationService(
            SurveillanceDeviceRepositoryPort repositoryPort) {
        return new PartialUpdateDeviceInformationService(repositoryPort);
    }

    @Bean
    public DeleteDeviceInformationService deleteDeviceInformationService(
            SurveillanceDeviceRepositoryPort repositoryPort) {
        return new DeleteDeviceInformationService(repositoryPort);
    }
}
