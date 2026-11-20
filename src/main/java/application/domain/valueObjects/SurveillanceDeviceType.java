package application.domain.valueObjects;

import java.util.Optional;

public enum SurveillanceDeviceType {

    STAR_LABS("STAR Labs", -500.0, -200.0, "star_labs", "starlabs", "star labs"),
    CCPD("CCPD", 100.0, -100.0, "ccpd"),
    STAR_SATELLITE("S.T.A.R. Satellite", 500.0, 100.0, "star_satellite", "starsatellite", "s.t.a.r. satellite", "star satellite", "satellite");

    private final String canonicalName;
    private final double x;
    private final double y;
    private final String[] aliases;

    SurveillanceDeviceType(String canonicalName, double x, double y, String... aliases) {
        this.canonicalName = canonicalName;
        this.x = x;
        this.y = y;
        this.aliases = aliases;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public static Optional<SurveillanceDeviceType> fromIdentifier(String rawIdentifier) {
        if (rawIdentifier == null || rawIdentifier.isBlank()) {
            return Optional.empty();
        }
        String normalized = rawIdentifier.trim().replaceAll("\\s+", " ");
        String compact = normalized.toLowerCase().replace(".", "").replace("-", "_").replace(" ", "_");

        for (SurveillanceDeviceType type : values()) {
            if (type.name().equalsIgnoreCase(normalized)
                    || type.canonicalName.equalsIgnoreCase(normalized)) {
                return Optional.of(type);
            }
            String typeCompact = type.canonicalName.toLowerCase().replace(".", "").replace("-", "_").replace(" ", "_");
            if (typeCompact.equals(compact)) {
                return Optional.of(type);
            }
            for (String alias : type.aliases) {
                String aliasCompact = alias.toLowerCase().replace(".", "").replace("-", "_").replace(" ", "_");
                if (alias.equalsIgnoreCase(normalized) || aliasCompact.equals(compact)) {
                    return Optional.of(type);
                }
            }
        }
        return Optional.empty();
    }
}
