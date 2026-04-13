package uspace.application.cruise.cabin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uspace.api.cruise.cabin.CabinAttributionCriteriaValidator;
import uspace.application.cruise.cabin.dtos.CabinAttributionDto;
import uspace.application.cruise.cabin.dtos.CabinAttributionResponseDto;
import uspace.domain.cruise.*;
import uspace.domain.cruise.booking.Booking;
import uspace.domain.cruise.booking.BookingId;
import uspace.domain.cruise.booking.traveler.Traveler;
import uspace.domain.cruise.cabin.*;
import uspace.domain.cruise.cabin.exceptions.InvalidCabinAttributionCriteriaException;
import uspace.domain.cruise.exceptions.CruiseNotFoundException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CabinAttributionServiceTest {

    private static final String CRUISE_ID = "CRUISE_ID";
    private static final CruiseId CRUISE_ID_OBJECT = new CruiseId(CRUISE_ID);
    private static final String CRITERIA_STRING = "bookingDateTime";
    private static final String INVALID_CRITERIA_STRING = "Invalid";
    private static final CabinAttributionCriteria CRITERIA = CabinAttributionCriteria.BOOKING_DATE_TIME;

    private static final String CATEGORY_STANDARD = "STANDARD";
    private static final String CABIN_ID_VALUE = "CABIN_1";
    private static final CabinId CABIN_ID = new CabinId(CABIN_ID_VALUE);

    private static final String BOOKING_ID = "BOOKING_1";
    private static final BookingId DOMAIN_BOOKING_ID = new BookingId(BOOKING_ID);
    private static final CabinType CABIN_TYPE = CabinType.STANDARD;

    private static final CabinAttribution DOMAIN_ATTR =
            new CabinAttribution(CABIN_ID, DOMAIN_BOOKING_ID, CABIN_TYPE);

    private static final CabinAttributionDto DTO =
            new CabinAttributionDto(CABIN_ID_VALUE, BOOKING_ID, CABIN_TYPE.name());

    private CruiseRepository cruiseRepository;
    private CabinRegistry cabinRegistry;
    private CabinBookingSorter sorter;
    private CabinAttributionEngine engine;
    private CabinAttributionAssembler assembler;
    private CabinAttributionCriteriaValidator validator;

    private CabinAttributionService service;

    @BeforeEach
    void setup() {
        cruiseRepository = mock(CruiseRepository.class);
        cabinRegistry = mock(CabinRegistry.class);
        sorter = mock(CabinBookingSorter.class);
        engine = mock(CabinAttributionEngine.class);
        assembler = mock(CabinAttributionAssembler.class);
        validator = mock(CabinAttributionCriteriaValidator.class);

        service = new CabinAttributionService(
                cruiseRepository,
                cabinRegistry,
                sorter,
                engine,
                assembler,
                validator
        );
    }

    @Test
    void givenValidInputs_whenGetAttributions_thenReturnResponseDto() {
        Booking booking = mock(Booking.class);
        Cruise cruise = mock(Cruise.class);

        when(booking.getTravelers()).thenReturn(List.of(mock(Traveler.class)));

        Map<String, List<String>> cabins =
                Map.of(CATEGORY_STANDARD, List.of(CABIN_ID_VALUE));

        List<Booking> travelerBookings = List.of(booking);
        List<Booking> sortedBookings = List.of(booking);

        List<CabinAttribution> domainList = List.of(DOMAIN_ATTR);
        List<CabinAttributionDto> dtoList = List.of(DTO);

        CabinAttributionResponseDto expectedResponse = new CabinAttributionResponseDto(dtoList);

        when(validator.validate(CRITERIA_STRING)).thenReturn(CRITERIA);
        when(cruiseRepository.findById(CRUISE_ID_OBJECT)).thenReturn(cruise);
        when(cruise.getBookings()).thenReturn(travelerBookings);
        when(cabinRegistry.getCabinsByCategory()).thenReturn(cabins);
        when(sorter.sort(travelerBookings, CRITERIA)).thenReturn(sortedBookings);
        when(engine.assign(sortedBookings, cabins)).thenReturn(domainList);
        when(assembler.fromDomain(domainList)).thenReturn(dtoList);
        when(assembler.toDto(dtoList)).thenReturn(expectedResponse);

        CabinAttributionResponseDto result =
                service.getAttributions(CRUISE_ID, CRITERIA_STRING);

        assertEquals(expectedResponse, result);
    }

    @Test
    void givenUnknownCruise_whenGetAttributions_thenThrowCruiseNotFoundException() {
        when(validator.validate(CRITERIA_STRING)).thenReturn(CRITERIA);
        when(cruiseRepository.findById(CRUISE_ID_OBJECT)).thenReturn(null);

        assertThrows(CruiseNotFoundException.class,
                () -> service.getAttributions(CRUISE_ID, CRITERIA_STRING));
    }

    @Test
    void givenInvalidCriteria_whenGetAttributions_thenThrowInvalidCabinAttributionCriteriaException() {


        when(validator.validate(INVALID_CRITERIA_STRING))
                .thenThrow(new InvalidCabinAttributionCriteriaException());

        assertThrows(InvalidCabinAttributionCriteriaException.class,
                () -> service.getAttributions(CRUISE_ID, INVALID_CRITERIA_STRING));
    }

    @Test
    void givenNoTravelerBookings_whenGetAttributions_thenReturnEmptyResponse() {
        Cruise cruise = mock(Cruise.class);

        when(cruise.getBookings()).thenReturn(List.of());

        when(validator.validate(CRITERIA_STRING)).thenReturn(CRITERIA);
        when(cruiseRepository.findById(CRUISE_ID_OBJECT)).thenReturn(cruise);

        when(sorter.sort(List.of(), CRITERIA)).thenReturn(List.of());
        when(engine.assign(List.of(), Map.of())).thenReturn(List.of());

        List<CabinAttributionDto> emptyDtos = List.of();
        CabinAttributionResponseDto emptyResponse = new CabinAttributionResponseDto(emptyDtos);

        when(assembler.fromDomain(List.of())).thenReturn(emptyDtos);
        when(assembler.toDto(emptyDtos)).thenReturn(emptyResponse);

        CabinAttributionResponseDto result =
                service.getAttributions(CRUISE_ID, CRITERIA_STRING);

        assertTrue(result.cabins.isEmpty());
    }

    @Test
    void givenValidFlow_whenGetAttributions_thenSortingAndEngineAreUsed() {
        Booking booking = mock(Booking.class);
        Cruise cruise = mock(Cruise.class);

        when(booking.getTravelers()).thenReturn(List.of(mock(Traveler.class)));

        Map<String, List<String>> cabins = Map.of();
        List<Booking> travelerBookings = List.of(booking);
        List<Booking> sorted = List.of(booking);

        when(validator.validate(CRITERIA_STRING)).thenReturn(CRITERIA);
        when(cruiseRepository.findById(CRUISE_ID_OBJECT)).thenReturn(cruise);
        when(cruise.getBookings()).thenReturn(travelerBookings);
        when(cabinRegistry.getCabinsByCategory()).thenReturn(cabins);
        when(sorter.sort(travelerBookings, CRITERIA)).thenReturn(sorted);
        when(engine.assign(sorted, cabins)).thenReturn(List.of());
        when(assembler.fromDomain(List.of())).thenReturn(List.of());
        when(assembler.toDto(List.of())).thenReturn(new CabinAttributionResponseDto(List.of()));

        service.getAttributions(CRUISE_ID, CRITERIA_STRING);

        verify(sorter).sort(travelerBookings, CRITERIA);
        verify(engine).assign(sorted, cabins);
    }
}
