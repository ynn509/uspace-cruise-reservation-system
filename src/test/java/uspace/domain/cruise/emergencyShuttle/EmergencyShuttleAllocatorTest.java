package uspace.domain.cruise.emergencyShuttle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uspace.domain.cruise.booking.traveler.AdultTraveler;
import uspace.domain.cruise.booking.traveler.StandardCostMultiplier;
import uspace.domain.cruise.booking.traveler.Traveler;
import uspace.domain.cruise.booking.traveler.TravelerId;
import uspace.domain.cruise.booking.traveler.TravelerName;
import uspace.domain.cruise.crew.crewMember.CrewMember;
import uspace.domain.cruise.crew.crewMember.CrewMemberName;
import uspace.domain.cruise.crew.crewMember.employeeId.EmployeeId;
import uspace.domain.cruise.money.Money;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmergencyShuttleAllocatorTest {
    private static final int RESCUE_SHIP_CAPACITY = 50;
    private static final int STANDARD_SHUTTLE_CAPACITY = 20;
    private static final int ONE_RESCUE_SHIP_LIMIT = 1;
    private static final int TWO_RESCUE_SHIPS_LIMIT = 2;
    private static final Money RESCUE_SHIP_COST = new Money(150_000);
    private static final Money STANDARD_SHUTTLE_COST = new Money(50_000);
    private static final String TRAVELER_ID_PREFIX = "TRAVELER_";
    private static final String TRAVELER_NAME_PREFIX = "Traveler ";
    private static final String EMPLOYEE_ID_PREFIX = "EMP_";
    private static final String CREW_NAME_PREFIX = "Crew ";

    private EmergencyShuttleAllocator emergencyShuttleAllocator;

    @BeforeEach
    void createAllocator() {
        emergencyShuttleAllocator = new EmergencyShuttleAllocator();
    }

    @Test
    void givenTravelersAndCrew_whenAllocateShuttles_thenRescueShipsAreFilledBeforeStandard() {
        EmergencyShuttleSpecifications specifications = specifications(RESCUE_SHIP_CAPACITY, STANDARD_SHUTTLE_CAPACITY, ONE_RESCUE_SHIP_LIMIT);
        List<Traveler> travelers = createTravelers(45);
        List<CrewMember> crewMembers = createCrewMembers(10);

        EmergencyShuttleManifest manifest = emergencyShuttleAllocator.allocate(specifications, travelers, crewMembers);

        assertEquals(2, manifest.getShuttles().size());
        EmergencyShuttle rescueShip = manifest.getShuttles().get(0);
        EmergencyShuttle standardShuttle = manifest.getShuttles().get(1);
        assertEquals(EmergencyShuttleType.RESCUE_SHIP, rescueShip.getType());
        assertEquals(EmergencyShuttleType.STANDARD_SHUTTLE, standardShuttle.getType());
        assertEquals(50, rescueShip.getTravelers().size() + rescueShip.getCrewMembers().size());
        assertEquals(5, standardShuttle.getTravelers().size() + standardShuttle.getCrewMembers().size());
    }

    @Test
    void givenTravelersExceedSingleRescueShipCapacity_whenAllocateShuttles_thenOpenNewRescueShipOnlyAfterPreviousIsFull() {
        EmergencyShuttleSpecifications specifications = specifications(3, 10, TWO_RESCUE_SHIPS_LIMIT);
        List<Traveler> travelers = createTravelers(4);

        EmergencyShuttleManifest manifest = emergencyShuttleAllocator.allocate(specifications, travelers, List.of());

        assertEquals(2, manifest.getShuttles().size());
        EmergencyShuttle firstRescueShip = manifest.getShuttles().get(0);
        EmergencyShuttle secondRescueShip = manifest.getShuttles().get(1);
        assertEquals(3, firstRescueShip.getTravelers().size());
        assertEquals(1, secondRescueShip.getTravelers().size());
    }

    @Test
    void givenConfiguredRescueShipLimitExceeded_whenAllocateShuttles_thenOverflowGoesToStandardShuttles() {
        EmergencyShuttleSpecifications specifications = specifications(2, 2, ONE_RESCUE_SHIP_LIMIT);
        List<Traveler> travelers = createTravelers(5);

        EmergencyShuttleManifest manifest = emergencyShuttleAllocator.allocate(specifications, travelers, List.of());

        assertEquals(3, manifest.getShuttles().size());
        assertEquals(EmergencyShuttleType.RESCUE_SHIP, manifest.getShuttles().get(0).getType());
        assertEquals(EmergencyShuttleType.STANDARD_SHUTTLE, manifest.getShuttles().get(1).getType());
        assertEquals(2, manifest.getShuttles().get(1).getTravelers().size());
    }

    @Test
    void givenBookingsAndCrew_whenBuildManifest_thenReserveSeatsAtBookingAndCrewAddition() {
        EmergencyShuttleSpecifications specifications = specifications(2, 2, ONE_RESCUE_SHIP_LIMIT);
        List<Traveler> travelers = createTravelers(1);
        List<CrewMember> crewMembers = createCrewMembers(1);

        EmergencyShuttleManifest manifest = emergencyShuttleAllocator.allocate(specifications, travelers, crewMembers);

        assertEquals(1, manifest.getShuttles().size());
        EmergencyShuttle rescueShip = manifest.getShuttles().get(0);
        assertEquals(1, rescueShip.getCrewMembers().size());
        assertEquals(1, rescueShip.getTravelers().size());
    }

    @Test
    void givenUsedShuttles_whenComputeTotalCost_thenSumOnlyUsedShuttles() {
        EmergencyShuttleSpecifications specifications = specifications(2, 2, TWO_RESCUE_SHIPS_LIMIT);
        List<Traveler> travelers = createTravelers(4);

        EmergencyShuttleManifest manifest = emergencyShuttleAllocator.allocate(specifications, travelers, List.of());

        assertEquals(2 * RESCUE_SHIP_COST.toFloat(), manifest.totalCost().toFloat());
    }

    @Test
    void givenEmptyShuttles_whenBuildManifest_thenExcludeEmptyShuttlesFromResult() {
        EmergencyShuttleSpecifications specifications = specifications(RESCUE_SHIP_CAPACITY, STANDARD_SHUTTLE_CAPACITY, ONE_RESCUE_SHIP_LIMIT);

        EmergencyShuttleManifest manifest = emergencyShuttleAllocator.allocate(specifications, List.of(), List.of());

        assertTrue(manifest.getShuttles().isEmpty());
        assertEquals(0f, manifest.totalCost().toFloat());
    }

    private EmergencyShuttleSpecifications specifications(int rescueShipCapacity, int standardShuttleCapacity, int rescueShipLimit) {
        EmergencyShuttleSpecification rescueShipSpecification = new EmergencyShuttleSpecification(
                EmergencyShuttleType.RESCUE_SHIP, rescueShipCapacity, RESCUE_SHIP_COST, rescueShipLimit);
        EmergencyShuttleSpecification standardShuttleSpecification = new EmergencyShuttleSpecification(
                EmergencyShuttleType.STANDARD_SHUTTLE, standardShuttleCapacity, STANDARD_SHUTTLE_COST, Integer.MAX_VALUE);
        Map<EmergencyShuttleType, EmergencyShuttleSpecification> specificationsByType = Map.of(
                EmergencyShuttleType.RESCUE_SHIP, rescueShipSpecification,
                EmergencyShuttleType.STANDARD_SHUTTLE, standardShuttleSpecification
        );
        return new EmergencyShuttleSpecifications(specificationsByType,
                List.of(EmergencyShuttleType.RESCUE_SHIP, EmergencyShuttleType.STANDARD_SHUTTLE));
    }

    private List<Traveler> createTravelers(int count) {
        List<Traveler> travelers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            travelers.add(new AdultTraveler(new TravelerId(TRAVELER_ID_PREFIX + i),
                    new TravelerName(TRAVELER_NAME_PREFIX + i),
                    new ArrayList<>(),
                    new StandardCostMultiplier(1),
                    new ArrayList<>()));
        }
        return travelers;
    }

    private List<CrewMember> createCrewMembers(int count) {
        List<CrewMember> crewMembers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            crewMembers.add(new CrewMember(new EmployeeId(EMPLOYEE_ID_PREFIX + i), new CrewMemberName(CREW_NAME_PREFIX + i)));
        }
        return crewMembers;
    }
}
