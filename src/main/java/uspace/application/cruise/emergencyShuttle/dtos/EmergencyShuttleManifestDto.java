package uspace.application.cruise.emergencyShuttle.dtos;

import java.util.List;

public class EmergencyShuttleManifestDto {
    public float cost;
    public List<EmergencyShuttleDto> emergencyShuttles;

    public EmergencyShuttleManifestDto() {
    }

    public EmergencyShuttleManifestDto(float cost, List<EmergencyShuttleDto> emergencyShuttles) {
        this.cost = cost;
        this.emergencyShuttles = emergencyShuttles;
    }
}
