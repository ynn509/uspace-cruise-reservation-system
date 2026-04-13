package uspace.domain.cruise.emergencyShuttle;

import java.util.List;
import java.util.Map;

public class EmergencyShuttleSpecifications {
    private final Map<EmergencyShuttleType, EmergencyShuttleSpecification> specificationsByType;
    private final List<EmergencyShuttleType> prioritizedTypes;

    public EmergencyShuttleSpecifications(Map<EmergencyShuttleType, EmergencyShuttleSpecification> specificationsByType,
                                          List<EmergencyShuttleType> prioritizedTypes) {
        this.specificationsByType = specificationsByType;
        this.prioritizedTypes = prioritizedTypes;
    }

    public EmergencyShuttleSpecification specificationFor(EmergencyShuttleType type) {
        return specificationsByType.get(type);
    }

    public List<EmergencyShuttleType> prioritizedTypes() {
        return prioritizedTypes;
    }
}
