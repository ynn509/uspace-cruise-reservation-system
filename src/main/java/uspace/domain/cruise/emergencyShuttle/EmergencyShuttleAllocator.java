package uspace.domain.cruise.emergencyShuttle;

import uspace.domain.cruise.booking.traveler.Traveler;
import uspace.domain.cruise.crew.crewMember.CrewMember;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class EmergencyShuttleAllocator {

    public EmergencyShuttleManifest allocate(EmergencyShuttleSpecifications specifications,
                                             List<Traveler> travelers,
                                             List<CrewMember> crewMembers) {
        List<EmergencyShuttlePassenger> passengers = mergePassengers(travelers, crewMembers);
        List<EmergencyShuttle> shuttles = new ArrayList<>();

        List<EmergencyShuttlePassenger> remainingPassengers = passengers;
        for (EmergencyShuttleType type : specifications.prioritizedTypes()) {
            EmergencyShuttleSpecification specification = specifications.specificationFor(type);
            remainingPassengers = allocatePassengersForType(remainingPassengers, specification, shuttles);
        }

        List<EmergencyShuttle> usedShuttles = shuttles.stream()
                .filter(shuttle -> !shuttle.hasNoOccupants())
                .toList();

        return new EmergencyShuttleManifest(usedShuttles);
    }

    private List<EmergencyShuttlePassenger> allocatePassengersForType(List<EmergencyShuttlePassenger> passengers,
                                                                      EmergencyShuttleSpecification specification,
                                                                      List<EmergencyShuttle> manifest) {
        List<EmergencyShuttlePassenger> remainingPassengers = new ArrayList<>();
        EmergencyShuttle currentShuttle = null;
        int createdShuttles = 0;

        Iterator<EmergencyShuttlePassenger> passengerIterator = passengers.iterator();

        while (passengerIterator.hasNext()) {
            EmergencyShuttlePassenger passenger = passengerIterator.next();

            if (currentShuttle == null) {
                if (hasReachedMaximumShuttles(createdShuttles, specification)) {
                    remainingPassengers.add(passenger);
                    continue;
                }

                currentShuttle = new EmergencyShuttle(specification);
                manifest.add(currentShuttle);
                createdShuttles++;
            }

            if (currentShuttle.canBoardPassenger()) {
                passenger.board(currentShuttle);
            } else if (hasReachedMaximumShuttles(createdShuttles, specification)) {
                remainingPassengers.add(passenger);
                addRemainingPassengers(passengerIterator, remainingPassengers);
                break;
            } else {
                currentShuttle = new EmergencyShuttle(specification);
                manifest.add(currentShuttle);
                createdShuttles++;
                passenger.board(currentShuttle);
            }
        }

        return remainingPassengers;
    }

    private boolean hasReachedMaximumShuttles(int createdShuttles, EmergencyShuttleSpecification specification) {
        return createdShuttles >= specification.getMaximumCount();
    }

    private List<EmergencyShuttlePassenger> mergePassengers(List<Traveler> travelers, List<CrewMember> crewMembers) {
        List<EmergencyShuttlePassenger> passengers = new ArrayList<>();

        List<Traveler> sortedTravelers = travelers.stream()
                .sorted(Comparator.comparing(traveler -> traveler.getId().toString()))
                .toList();
        sortedTravelers.stream()
                .map(TravelerPassenger::new)
                .forEach(passengers::add);

        List<CrewMember> sortedCrewMembers = crewMembers.stream()
                .sorted(Comparator.comparing(crewMember -> crewMember.getEmployeeId().toString()))
                .toList();
        sortedCrewMembers.stream()
                .map(CrewMemberPassenger::new)
                .forEach(passengers::add);

        return passengers;
    }

    private void addRemainingPassengers(Iterator<EmergencyShuttlePassenger> passengerIterator, List<EmergencyShuttlePassenger> remainingPassengers) {
        passengerIterator.forEachRemaining(remainingPassengers::add);
    }

    private interface EmergencyShuttlePassenger {
        void board(EmergencyShuttle shuttle);
    }

    private static class TravelerPassenger implements EmergencyShuttlePassenger {
        private final Traveler traveler;

        public TravelerPassenger(Traveler traveler) {
            this.traveler = traveler;
        }

        @Override
        public void board(EmergencyShuttle shuttle) {
            shuttle.boardTraveler(traveler);
        }
    }

    private static class CrewMemberPassenger implements EmergencyShuttlePassenger {
        private final CrewMember crewMember;

        public CrewMemberPassenger(CrewMember crewMember) {
            this.crewMember = crewMember;
        }

        @Override
        public void board(EmergencyShuttle shuttle) {
            shuttle.boardCrewMember(crewMember);
        }
    }
}
