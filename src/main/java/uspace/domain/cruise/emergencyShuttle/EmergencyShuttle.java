package uspace.domain.cruise.emergencyShuttle;

import uspace.domain.cruise.booking.traveler.Traveler;
import uspace.domain.cruise.crew.crewMember.CrewMember;
import uspace.domain.cruise.money.Money;

import java.util.ArrayList;
import java.util.List;

public class EmergencyShuttle {
    private static final String FULL_SHUTTLE_TRAVELER_ERROR = "Cannot board traveler on a full shuttle";
    private static final String FULL_SHUTTLE_CREW_ERROR = "Cannot board crew member on a full shuttle";
    private final EmergencyShuttleSpecification specification;
    private final List<Traveler> travelers;
    private final List<CrewMember> crewMembers;

    public EmergencyShuttle(EmergencyShuttleSpecification specification) {
        this.specification = specification;
        this.travelers = new ArrayList<>();
        this.crewMembers = new ArrayList<>();
    }

    public EmergencyShuttleType getType() {
        return specification.getType();
    }

    public Money getCost() {
        return specification.getCost();
    }

    public List<Traveler> getTravelers() {
        return List.copyOf(travelers);
    }

    public List<CrewMember> getCrewMembers() {
        return List.copyOf(crewMembers);
    }

    public boolean hasNoOccupants() {
        return travelers.isEmpty() && crewMembers.isEmpty();
    }

    public boolean canBoardPassenger() {
        return occupantsCount() < specification.getCapacity();
    }

    public void boardTraveler(Traveler traveler) {
        if (!canBoardPassenger()) {
            throw new IllegalStateException(FULL_SHUTTLE_TRAVELER_ERROR);
        }

        travelers.add(traveler);
    }

    public void boardCrewMember(CrewMember crewMember) {
        if (!canBoardPassenger()) {
            throw new IllegalStateException(FULL_SHUTTLE_CREW_ERROR);
        }

        crewMembers.add(crewMember);
    }

    private int occupantsCount() {
        return travelers.size() + crewMembers.size();
    }
}
