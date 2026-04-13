package uspace.api.cruise.emergencyShuttle;

import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import uspace.api.exceptions.ErrorResponse;
import uspace.api.exceptions.mappers.uspace.CatchAllExceptionMapper;
import uspace.api.exceptions.mappers.uspace.CruiseNotFoundExceptionMapper;
import uspace.application.cruise.emergencyShuttle.EmergencyShuttleAssembler;
import uspace.application.cruise.emergencyShuttle.EmergencyShuttleConfigurationProperties;
import uspace.application.cruise.emergencyShuttle.EmergencyShuttleService;
import uspace.application.cruise.emergencyShuttle.dtos.EmergencyShuttleManifestDto;
import uspace.domain.cruise.Cruise;
import uspace.domain.cruise.CruiseId;
import uspace.domain.cruise.CruiseRepository;
import uspace.domain.cruise.booking.Booking;
import uspace.domain.cruise.booking.BookingId;
import uspace.domain.cruise.booking.CruiseBookings;
import uspace.domain.cruise.booking.CruiseBookingsId;
import uspace.domain.cruise.booking.invoice.InvoiceFactory;
import uspace.domain.cruise.booking.traveler.AdultTraveler;
import uspace.domain.cruise.booking.traveler.StandardCostMultiplier;
import uspace.domain.cruise.booking.traveler.Traveler;
import uspace.domain.cruise.booking.traveler.TravelerId;
import uspace.domain.cruise.booking.traveler.TravelerName;
import uspace.domain.cruise.cabin.CabinAvailabilities;
import uspace.domain.cruise.cabin.CabinAvailabilitiesId;
import uspace.domain.cruise.cabin.CabinPriceRegistry;
import uspace.domain.cruise.cabin.CabinType;
import uspace.domain.cruise.crew.Crew;
import uspace.domain.cruise.crew.CrewId;
import uspace.domain.cruise.crew.CrewMemberCollection;
import uspace.domain.cruise.crew.crewMember.CrewMember;
import uspace.domain.cruise.crew.crewMember.CrewMemberName;
import uspace.domain.cruise.crew.crewMember.employeeId.EmployeeId;
import uspace.domain.cruise.crew.crewMember.employeeId.EmployeeIdValidatorRegex;
import uspace.domain.cruise.dateTime.CruiseDateTime;
import uspace.domain.cruise.emergencyShuttle.EmergencyShuttleAllocator;
import uspace.domain.cruise.emergencyShuttle.EmergencyShuttleType;
import uspace.domain.cruise.emergencyShuttle.configuration.EmergencyShuttleConfiguration;
import uspace.domain.cruise.itinerary.Itinerary;
import uspace.domain.cruise.itinerary.ItineraryId;
import uspace.domain.cruise.money.Money;
import uspace.domain.cruise.zeroGravityExperience.ZeroGravityExperience;
import uspace.domain.cruise.zeroGravityExperience.ZeroGravityExperienceId;
import uspace.infra.persistence.inMemory.InMemoryCruiseRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("component")
class EmergencyShuttleResourceTest extends JerseyTest {
    private static final String CRUISE_WITH_MANIFEST = "CRUISE_WITH_MANIFEST";
    private static final String EMPTY_CRUISE = "EMPTY_CRUISE";
    private static final String NON_EXISTING_CRUISE = "NON_EXISTING_CRUISE";
    private static final String BASE_MANIFEST_PATH = "/cruises/%s/emergencyShuttles";
    private static final int TRAVELER_COUNT_WITH_MANIFEST = 52;
    private static final int CREW_COUNT_WITH_MANIFEST = 2;
    private static final int EMPTY_TRAVELER_COUNT = 0;
    private static final int EMPTY_CREW_COUNT = 0;
    private static final float EXPECTED_TOTAL_COST = 200_000f;
    private static final int EXPECTED_RESCUE_OCCUPANTS = 50;
    private static final int EXPECTED_STANDARD_OCCUPANTS = 4;
    private static final String RESCUE_SHIP_TYPE = EmergencyShuttleType.RESCUE_SHIP.name();
    private static final String STANDARD_SHUTTLE_TYPE = EmergencyShuttleType.STANDARD_SHUTTLE.name();
    private static final String CRUISE_NOT_FOUND_ERROR = "CRUISE_NOT_FOUND";
    private static final String TRAVELER_ID_PREFIX = "TRAVELER_";
    private static final String TRAVELER_NAME_PREFIX = "Traveler ";
    private static final String BOOKING_ID_PREFIX = "BOOKING_";
    private static final String EMPLOYEE_ID_PREFIX = "EMP_";
    private static final String CREW_NAME_PREFIX = "Crew ";
    private static final int STANDARD_CABIN_AVAILABILITY = 200;
    private static final int DELUXE_CABIN_AVAILABILITY = 100;
    private static final int SUITE_CABIN_AVAILABILITY = 50;
    private static final Money ZERO_COST = Money.zero();
    private static final Money STANDARD_CABIN_COST = new Money(1000);
    private static final Money DELUXE_CABIN_COST = new Money(2000);
    private static final Money SUITE_CABIN_COST = new Money(3000);
    private static final int ZERO_GRAVITY_CAPACITY = 10;
    private static final int MINIMAL_DAYS_FOR_EXCURSION = 1;
    private InMemoryCruiseRepository cruiseRepository;

    @Override
    protected Application configure() {
        cruiseRepository = new InMemoryCruiseRepository();
        cruiseRepository.save(createCruiseWithPassengers(CRUISE_WITH_MANIFEST, TRAVELER_COUNT_WITH_MANIFEST, CREW_COUNT_WITH_MANIFEST));
        cruiseRepository.save(createCruiseWithPassengers(EMPTY_CRUISE, EMPTY_TRAVELER_COUNT, EMPTY_CREW_COUNT));

        EmergencyShuttleConfiguration emergencyShuttleConfiguration = new EmergencyShuttleConfigurationProperties();
        EmergencyShuttleAllocator allocator = new EmergencyShuttleAllocator();
        EmergencyShuttleAssembler assembler = new EmergencyShuttleAssembler();

        return new ResourceConfig()
                .register(JacksonFeature.withoutExceptionMappers())
                .register(CruiseNotFoundExceptionMapper.class)
                .register(CatchAllExceptionMapper.class)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(cruiseRepository).to(CruiseRepository.class);
                        bind(emergencyShuttleConfiguration).to(EmergencyShuttleConfiguration.class);
                        bind(allocator).to(EmergencyShuttleAllocator.class);
                        bind(assembler).to(EmergencyShuttleAssembler.class);
                        bindAsContract(EmergencyShuttleService.class);
                    }
                })
                .register(EmergencyShuttleResource.class);
    }

    @Test
    void givenExistingCruiseWithTravelersAndCrew_whenGetEmergencyShuttles_thenReturn200WithManifestAndCost() {
        Response response = target(String.format(BASE_MANIFEST_PATH, CRUISE_WITH_MANIFEST)).request().get();

        assertEquals(200, response.getStatus());
        EmergencyShuttleManifestDto manifestDto = response.readEntity(EmergencyShuttleManifestDto.class);
        assertEquals(2, manifestDto.emergencyShuttles.size());
        assertEquals(RESCUE_SHIP_TYPE, manifestDto.emergencyShuttles.get(0).type);
        assertEquals(STANDARD_SHUTTLE_TYPE, manifestDto.emergencyShuttles.get(1).type);
        assertEquals(EXPECTED_TOTAL_COST, manifestDto.cost);
        int rescueOccupants = manifestDto.emergencyShuttles.get(0).travelers.size() + manifestDto.emergencyShuttles.get(0).crewMembers.size();
        int standardOccupants = manifestDto.emergencyShuttles.get(1).travelers.size() + manifestDto.emergencyShuttles.get(1).crewMembers.size();
        assertEquals(EXPECTED_RESCUE_OCCUPANTS, rescueOccupants);
        assertEquals(EXPECTED_STANDARD_OCCUPANTS, standardOccupants);
    }

    @Test
    void givenCruiseWithoutTravelersOrCrew_whenGetEmergencyShuttles_thenReturn200WithEmptyManifest() {
        Response response = target(String.format(BASE_MANIFEST_PATH, EMPTY_CRUISE)).request().get();

        assertEquals(200, response.getStatus());
        EmergencyShuttleManifestDto manifestDto = response.readEntity(EmergencyShuttleManifestDto.class);
        assertEquals(0f, manifestDto.cost);
        assertEquals(0, manifestDto.emergencyShuttles.size());
    }

    @Test
    void givenNonExistingCruise_whenGetEmergencyShuttles_thenReturn404ErrorPayload() {
        Response response = target(String.format(BASE_MANIFEST_PATH, NON_EXISTING_CRUISE)).request().get();

        assertEquals(404, response.getStatus());
        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(CRUISE_NOT_FOUND_ERROR, errorResponse.error);
    }

    private Cruise createCruiseWithPassengers(String cruiseId, int travelerCount, int crewCount) {
        CruiseBookings cruiseBookings = new CruiseBookings(new CruiseBookingsId(), createBookings(travelerCount));
        Crew crew = createCrew(crewCount);

        return new Cruise(new CruiseId(cruiseId),
                new CruiseDateTime(LocalDateTime.now()),
                new CruiseDateTime(LocalDateTime.now().plusDays(1)),
                defaultCabinPriceRegistry(),
                defaultCabinAvailabilities(),
                cruiseBookings,
                new ZeroGravityExperience(new ZeroGravityExperienceId(), ZERO_GRAVITY_CAPACITY),
                new Itinerary(new ItineraryId(), new ArrayList<>(), MINIMAL_DAYS_FOR_EXCURSION),
                crew);
    }

    private Map<BookingId, Booking> createBookings(int travelerCount) {
        Map<BookingId, Booking> bookings = new HashMap<>();
        if (travelerCount == 0) {
            return bookings;
        }

        List<Traveler> travelers = new ArrayList<>();
        for (int i = 0; i < travelerCount; i++) {
            travelers.add(new AdultTraveler(new TravelerId(TRAVELER_ID_PREFIX + i), new TravelerName(TRAVELER_NAME_PREFIX + i),
                    new ArrayList<>(), new StandardCostMultiplier(1), new ArrayList<>()));
        }

        Booking booking = new Booking(new BookingId(BOOKING_ID_PREFIX + travelerCount), travelers, CabinType.STANDARD,
                new CruiseDateTime(LocalDateTime.now().minusDays(1)),
                new InvoiceFactory().create(ZERO_COST));
        bookings.put(booking.getId(), booking);
        return bookings;
    }

    private Crew createCrew(int crewCount) {
        Map<EmployeeId, CrewMember> crewMembers = new HashMap<>();
        for (int i = 0; i < crewCount; i++) {
            CrewMember crewMember = new CrewMember(new EmployeeId(EMPLOYEE_ID_PREFIX + i), new CrewMemberName(CREW_NAME_PREFIX + i));
            crewMembers.put(crewMember.getEmployeeId(), crewMember);
        }
        return new Crew(new CrewId(), new CrewMemberCollection(crewMembers), EmployeeIdValidatorRegex.for3UppercaseLettersFollowedBy3Digits());
    }

    private CabinPriceRegistry defaultCabinPriceRegistry() {
        return new CabinPriceRegistry(new HashMap<>(Map.of(
                CabinType.STANDARD, STANDARD_CABIN_COST,
                CabinType.DELUXE, DELUXE_CABIN_COST,
                CabinType.SUITE, SUITE_CABIN_COST
        )));
    }

    private CabinAvailabilities defaultCabinAvailabilities() {
        Map<CabinType, Integer> availabilities = new HashMap<>();
        availabilities.put(CabinType.STANDARD, STANDARD_CABIN_AVAILABILITY);
        availabilities.put(CabinType.DELUXE, DELUXE_CABIN_AVAILABILITY);
        availabilities.put(CabinType.SUITE, SUITE_CABIN_AVAILABILITY);
        return new CabinAvailabilities(new CabinAvailabilitiesId(), availabilities);
    }
}
