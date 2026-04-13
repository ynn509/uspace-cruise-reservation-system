package uspace.application.cruise.emergencyShuttle;

import uspace.application.cruise.emergencyShuttle.dtos.EmergencyShuttleCrewMemberDto;
import uspace.application.cruise.emergencyShuttle.dtos.EmergencyShuttleDto;
import uspace.application.cruise.emergencyShuttle.dtos.EmergencyShuttleManifestDto;
import uspace.application.cruise.emergencyShuttle.dtos.EmergencyShuttleTravelerDto;
import uspace.domain.cruise.emergencyShuttle.EmergencyShuttle;
import uspace.domain.cruise.emergencyShuttle.EmergencyShuttleManifest;

import java.util.List;

public class EmergencyShuttleAssembler {

    public EmergencyShuttleManifestDto toDto(EmergencyShuttleManifest manifest) {
        List<EmergencyShuttleDto> shuttleDtos = manifest.getShuttles().stream()
                .map(this::toDto)
                .toList();

        return new EmergencyShuttleManifestDto(roundToNearestInteger(manifest.totalCost().toFloat()), shuttleDtos);
    }

    private EmergencyShuttleDto toDto(EmergencyShuttle shuttle) {
        List<EmergencyShuttleTravelerDto> travelers = shuttle.getTravelers().stream()
                .map(traveler -> new EmergencyShuttleTravelerDto(traveler.getId().toString(),
                        traveler.getName().toString()))
                .toList();
        List<EmergencyShuttleCrewMemberDto> crewMembers = shuttle.getCrewMembers().stream()
                .map(crewMember -> new EmergencyShuttleCrewMemberDto(crewMember.getEmployeeId().toString(),
                        crewMember.getName().toString()))
                .toList();
        return new EmergencyShuttleDto(shuttle.getType().toString(), travelers, crewMembers);
    }

    private int roundToNearestInteger(float value) {
        return Math.round(value);
    }
}
