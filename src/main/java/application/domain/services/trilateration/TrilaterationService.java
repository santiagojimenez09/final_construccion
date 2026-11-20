package application.domain.services.trilateration;

import application.domain.exceptions.InsufficientInformationException;
import application.domain.exceptions.TrilaterationCalculationException;
import application.domain.models.Position;
import application.domain.models.SurveillanceDevice;
import application.domain.valueObjects.SurveillanceDeviceType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TrilaterationService {

    private static final double EPSILON = 1e-6;

    public Position getLocation(List<SurveillanceDevice> devices) {
        if (devices == null || devices.size() < 3) {
            throw new InsufficientInformationException(
                    "Se requiere la información de los tres dispositivos de vigilancia (STAR Labs, CCPD y S.T.A.R. Satellite) para calcular la ubicación.");
        }

        Map<SurveillanceDeviceType, Double> distancesByDevice = new EnumMap<>(SurveillanceDeviceType.class);

        for (SurveillanceDevice device : devices) {
            if (device == null || device.getDeviceType() == null) {
                throw new InsufficientInformationException("Dispositivo de vigilancia inválido o sin identificación reconocida.");
            }
            Double dist = device.getDistance();
            if (dist == null || Double.isNaN(dist) || Double.isInfinite(dist) || dist < 0) {
                throw new TrilaterationCalculationException(
                        "La distancia reportada por el dispositivo '" + device.getDeviceType().getCanonicalName() + "' debe ser un número mayor o igual a cero.");
            }
            distancesByDevice.put(device.getDeviceType(), dist);
        }

        if (distancesByDevice.size() < 3) {
            throw new InsufficientInformationException(
                    "Falta información de al menos uno de los tres dispositivos de vigilancia requeridos (STAR Labs, CCPD, S.T.A.R. Satellite).");
        }

        double r1 = distancesByDevice.get(SurveillanceDeviceType.STAR_LABS);
        double r2 = distancesByDevice.get(SurveillanceDeviceType.CCPD);
        double r3 = distancesByDevice.get(SurveillanceDeviceType.STAR_SATELLITE);

        return calculatePosition(
                SurveillanceDeviceType.STAR_LABS.getX(), SurveillanceDeviceType.STAR_LABS.getY(), r1,
                SurveillanceDeviceType.CCPD.getX(), SurveillanceDeviceType.CCPD.getY(), r2,
                SurveillanceDeviceType.STAR_SATELLITE.getX(), SurveillanceDeviceType.STAR_SATELLITE.getY(), r3
        );
    }

    public Position calculatePosition(double x1, double y1, double r1,
                                      double x2, double y2, double r2,
                                      double x3, double y3, double r3) {
        double a = 2 * (x2 - x1);
        double b = 2 * (y2 - y1);
        double c = (r1 * r1) - (r2 * r2) - (x1 * x1) + (x2 * x2) - (y1 * y1) + (y2 * y2);

        double d = 2 * (x3 - x2);
        double e = 2 * (y3 - y2);
        double f = (r2 * r2) - (r3 * r3) - (x2 * x2) + (x3 * x3) - (y2 * y2) + (y3 * y3);

        double determinant = (a * e) - (b * d);
        if (Math.abs(determinant) < EPSILON) {
            throw new TrilaterationCalculationException("Las posiciones de los dispositivos son colineales; no es posible triangular la ubicación.");
        }

        double x = ((c * e) - (f * b)) / determinant;
        double y = ((a * f) - (d * c)) / determinant;

        double roundedX = Math.round(x * 100.0) / 100.0;
        double roundedY = Math.round(y * 100.0) / 100.0;

        return new Position(roundedX, roundedY);
    }
}
