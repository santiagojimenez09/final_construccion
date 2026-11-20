package application.domain.services;

import application.domain.exceptions.InsufficientInformationException;
import application.domain.exceptions.MessageReconstructionException;
import application.domain.exceptions.TrilaterationCalculationException;
import application.domain.models.Position;
import application.domain.models.SurveillanceDevice;
import application.domain.services.reconstruction.MessageReconstructionService;
import application.domain.services.trilateration.TrilaterationService;
import application.domain.valueObjects.SurveillanceDeviceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrilaterationAndReconstructionDomainTest {

    private TrilaterationService trilaterationService;
    private MessageReconstructionService messageReconstructionService;

    @BeforeEach
    void setUp() {
        trilaterationService = new TrilaterationService();
        messageReconstructionService = new MessageReconstructionService();
    }

    @Test
    void shouldCalculateExactPositionViaTrilateration() {
        // True target position: (-100.0, 75.5)
        // STAR Labs (-500, -200) -> dx = 400, dy = 275.5 -> r1 = sqrt(160000 + 75900.25) = sqrt(235900.25) = 485.695635146
        // CCPD (100, -100)       -> dx = -200, dy = 175.5 -> r2 = sqrt(40000 + 30800.25) = sqrt(70800.25)   = 266.083163691
        // Satellite (500, 100)   -> dx = -600, dy = -24.5 -> r3 = sqrt(360000 + 600.25)  = sqrt(360600.25)  = 600.499999999
        double r1 = Math.sqrt(235900.25);
        double r2 = Math.sqrt(70800.25);
        double r3 = Math.sqrt(360600.25);

        List<SurveillanceDevice> devices = Arrays.asList(
                new SurveillanceDevice("STAR Labs", SurveillanceDeviceType.STAR_LABS, new Position(-500, -200), r1, List.of("este", "", "")),
                new SurveillanceDevice("CCPD", SurveillanceDeviceType.CCPD, new Position(100, -100), r2, List.of("", "es", "")),
                new SurveillanceDevice("S.T.A.R. Satellite", SurveillanceDeviceType.STAR_SATELLITE, new Position(500, 100), r3, List.of("", "", "mensaje"))
        );

        Position position = trilaterationService.getLocation(devices);

        assertNotNull(position);
        assertEquals(-100.0, position.getX(), 0.01);
        assertEquals(75.5, position.getY(), 0.01);
    }

    @Test
    void shouldReconstructMessageIgnoringEmptyFragments() {
        List<SurveillanceDevice> devices = Arrays.asList(
                new SurveillanceDevice("STAR Labs", SurveillanceDeviceType.STAR_LABS, new Position(-500, -200), 100.0,
                        Arrays.asList("este", "", "", "mensaje", "")),
                new SurveillanceDevice("CCPD", SurveillanceDeviceType.CCPD, new Position(100, -100), 115.5,
                        Arrays.asList("", "es", "", "", "secreto")),
                new SurveillanceDevice("S.T.A.R. Satellite", SurveillanceDeviceType.STAR_SATELLITE, new Position(500, 100), 142.7,
                        Arrays.asList("este", "", "un", "", ""))
        );

        String message = messageReconstructionService.getMessage(devices);
        assertEquals("este es un mensaje secreto", message);
    }

    @Test
    void shouldFailTrilaterationWithNegativeDistance() {
        List<SurveillanceDevice> devices = Arrays.asList(
                new SurveillanceDevice("STAR Labs", SurveillanceDeviceType.STAR_LABS, new Position(-500, -200), -10.0, List.of("a")),
                new SurveillanceDevice("CCPD", SurveillanceDeviceType.CCPD, new Position(100, -100), 100.0, List.of("a")),
                new SurveillanceDevice("S.T.A.R. Satellite", SurveillanceDeviceType.STAR_SATELLITE, new Position(500, 100), 100.0, List.of("a"))
        );

        assertThrows(TrilaterationCalculationException.class, () -> trilaterationService.getLocation(devices));
    }

    @Test
    void shouldFailReconstructionWhenAllWordsAreEmpty() {
        List<SurveillanceDevice> devices = Arrays.asList(
                new SurveillanceDevice("STAR Labs", SurveillanceDeviceType.STAR_LABS, new Position(-500, -200), 100.0, Arrays.asList("", "")),
                new SurveillanceDevice("CCPD", SurveillanceDeviceType.CCPD, new Position(100, -100), 100.0, Arrays.asList("", "")),
                new SurveillanceDevice("S.T.A.R. Satellite", SurveillanceDeviceType.STAR_SATELLITE, new Position(500, 100), 100.0, Arrays.asList("", ""))
        );

        assertThrows(MessageReconstructionException.class, () -> messageReconstructionService.getMessage(devices));
    }

    @Test
    void shouldFailWhenLessThanThreeDevicesProvided() {
        List<SurveillanceDevice> devices = Arrays.asList(
                new SurveillanceDevice("STAR Labs", SurveillanceDeviceType.STAR_LABS, new Position(-500, -200), 100.0, List.of("hola"))
        );

        assertThrows(InsufficientInformationException.class, () -> trilaterationService.getLocation(devices));
    }
}
