package uspace.domain.cruise.emergencyShuttle.configuration;

import uspace.domain.cruise.CruiseId;
import uspace.domain.cruise.emergencyShuttle.EmergencyShuttleSpecifications;

public interface EmergencyShuttleConfiguration {
    EmergencyShuttleSpecifications specificationsFor(CruiseId cruiseId);
}
