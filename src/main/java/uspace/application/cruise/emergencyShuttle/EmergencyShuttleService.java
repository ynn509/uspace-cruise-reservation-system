package uspace.application.cruise.emergencyShuttle;

import jakarta.inject.Inject;
import uspace.application.cruise.emergencyShuttle.dtos.EmergencyShuttleManifestDto;
import uspace.domain.cruise.Cruise;
import uspace.domain.cruise.CruiseId;
import uspace.domain.cruise.CruiseRepository;
import uspace.domain.cruise.booking.traveler.Traveler;
import uspace.domain.cruise.crew.crewMember.CrewMember;
import uspace.domain.cruise.emergencyShuttle.EmergencyShuttleAllocator;
import uspace.domain.cruise.emergencyShuttle.EmergencyShuttleManifest;
import uspace.domain.cruise.emergencyShuttle.EmergencyShuttleSpecifications;
import uspace.domain.cruise.emergencyShuttle.configuration.EmergencyShuttleConfiguration;
import uspace.domain.cruise.exceptions.CruiseNotFoundException;

import java.util.List;

public class EmergencyShuttleService {

    private final CruiseRepository cruiseRepository;
    private final EmergencyShuttleConfiguration emergencyShuttleConfiguration;
    private final EmergencyShuttleAllocator emergencyShuttleAllocator;
    private final EmergencyShuttleAssembler emergencyShuttleAssembler;

    @Inject
    public EmergencyShuttleService(CruiseRepository cruiseRepository,
                                   EmergencyShuttleConfiguration emergencyShuttleConfiguration,
                                   EmergencyShuttleAllocator emergencyShuttleAllocator,
                                   EmergencyShuttleAssembler emergencyShuttleAssembler) {
        this.cruiseRepository = cruiseRepository;
        this.emergencyShuttleConfiguration = emergencyShuttleConfiguration;
        this.emergencyShuttleAllocator = emergencyShuttleAllocator;
        this.emergencyShuttleAssembler = emergencyShuttleAssembler;
    }

    public EmergencyShuttleManifestDto getManifest(String cruiseId) {
        Cruise cruise = cruiseRepository.findById(new CruiseId(cruiseId));
        if (cruise == null) {
            throw new CruiseNotFoundException();
        }

        EmergencyShuttleSpecifications specifications = emergencyShuttleConfiguration.specificationsFor(cruise.getId());
        List<Traveler> travelers = cruise.getTravelers();
        List<CrewMember> crewMembers = cruise.getCrewMembers();

        EmergencyShuttleManifest manifest = emergencyShuttleAllocator.allocate(specifications, travelers, crewMembers);

        return emergencyShuttleAssembler.toDto(manifest);
    }
}
