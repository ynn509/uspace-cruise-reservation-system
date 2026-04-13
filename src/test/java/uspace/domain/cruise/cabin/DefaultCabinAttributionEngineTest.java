package uspace.domain.cruise.cabin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uspace.domain.cruise.booking.Booking;
import uspace.domain.cruise.booking.BookingId;
import uspace.domain.cruise.booking.invoice.Invoice;
import uspace.domain.cruise.booking.traveler.Traveler;
import uspace.domain.cruise.dateTime.CruiseDateTime;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DefaultCabinAttributionEngineTest {

    private static final Invoice ANY_INVOICE = new Invoice();
    private static final CruiseDateTime ANY_DATE =
            new CruiseDateTime(LocalDateTime.parse("2025-01-01T10:00:00"));

    private static final String BOOKING_ID_1 = "B1";
    private static final String BOOKING_ID_2 = "B2";
    private static final String BOOKING_ID_3 = "B3";

    private static final String CABIN_ID_1 = "C1";
    private static final String CABIN_ID_2 = "C2";
    private static final String CABIN_ID_3 = "C3";

    private static final String CATEGORY_STANDARD = "standard";
    private static final String CATEGORY_DELUXE = "deluxe";

    private DefaultCabinAttributionEngine engine;

    @BeforeEach
    void setup() {
        engine = new DefaultCabinAttributionEngine();
    }

    private Booking booking(String id, CabinType type) {
        List<Traveler> travelers = List.of(mock(Traveler.class));
        return new Booking(new BookingId(id), travelers, type, ANY_DATE, ANY_INVOICE);
    }

    private Map<String, List<String>> cabins(String... ids) {
        return Map.of(DefaultCabinAttributionEngineTest.CATEGORY_STANDARD, Arrays.asList(ids));
    }

    @Test
    void givenBookingsAndCabins_whenAssign_thenCorrectCabinsAreAttributed() {
        Booking booking1 = booking(BOOKING_ID_1, CabinType.STANDARD);
        Booking booking2 = booking(BOOKING_ID_2, CabinType.STANDARD);

        Map<String, List<String>> cabinMap =
                cabins(CABIN_ID_1, CABIN_ID_2);

        List<CabinAttribution> result =
                engine.assign(List.of(booking1, booking2), cabinMap);

        assertEquals(2, result.size());
        assertEquals(new CabinId(CABIN_ID_1), result.get(0).getCabinId());
        assertEquals(BOOKING_ID_1, result.get(0).getBookingId().toString());
        assertEquals(CabinType.STANDARD, result.get(0).getCabinType());
    }

    @Test
    void givenMoreBookingsThanCabins_whenAssign_thenExtraBookingsAreIgnored() {
        Booking booking1 = booking(BOOKING_ID_1, CabinType.STANDARD);
        Booking booking2 = booking(BOOKING_ID_2, CabinType.STANDARD);
        Booking booking3 = booking(BOOKING_ID_3, CabinType.STANDARD);

        Map<String, List<String>> cabinMap =
                cabins(CABIN_ID_1, CABIN_ID_2);

        List<CabinAttribution> result =
                engine.assign(List.of(booking1, booking2, booking3), cabinMap);

        assertEquals(2, result.size());
        assertEquals(new CabinId(CABIN_ID_1), result.get(0).getCabinId());
        assertEquals(new CabinId(CABIN_ID_2), result.get(1).getCabinId());
    }

    @Test
    void givenMoreCabinsThanBookings_whenAssign_thenOnlyBookingsAreAssigned() {
        Booking booking1 = booking(BOOKING_ID_1, CabinType.STANDARD);

        Map<String, List<String>> cabinMap =
                cabins(CABIN_ID_1, CABIN_ID_2, CABIN_ID_3);

        List<CabinAttribution> result =
                engine.assign(List.of(booking1), cabinMap);

        assertEquals(1, result.size());
        assertEquals(new CabinId(CABIN_ID_1), result.get(0).getCabinId());
    }

    @Test
    void givenDifferentCategories_whenAssign_thenBookingsReceiveCabinsFromMatchingCategory() {
        Booking standardBooking = booking(BOOKING_ID_1, CabinType.STANDARD);
        Booking deluxeBooking = booking(BOOKING_ID_2, CabinType.DELUXE);

        Map<String, List<String>> cabinMap = new HashMap<>();
        cabinMap.put(CATEGORY_STANDARD, List.of(CABIN_ID_1));
        cabinMap.put(CATEGORY_DELUXE, List.of(CABIN_ID_2));

        List<Booking> bookings = List.of(standardBooking, deluxeBooking);

        List<CabinAttribution> result = engine.assign(bookings, cabinMap);

        assertEquals(2, result.size());
        assertEquals(new CabinId(CABIN_ID_1), result.get(0).getCabinId());
        assertEquals(BOOKING_ID_1, result.get(0).getBookingId().toString());

        assertEquals(new CabinId(CABIN_ID_2), result.get(1).getCabinId());
        assertEquals(BOOKING_ID_2, result.get(1).getBookingId().toString());
    }

    @Test
    void givenNoBookings_whenAssign_thenReturnEmptyList() {
        Map<String, List<String>> cabinMap =
                cabins(CABIN_ID_1);

        List<CabinAttribution> result = engine.assign(List.of(), cabinMap);

        assertTrue(result.isEmpty());
    }

    @Test
    void givenEmptyCabins_whenAssign_thenReturnEmptyList() {
        Booking booking1 = booking(BOOKING_ID_1, CabinType.STANDARD);

        List<CabinAttribution> result =
                engine.assign(List.of(booking1), Collections.emptyMap());

        assertTrue(result.isEmpty());
    }

    @Test
    void givenCabinsInOrder_whenAssign_thenOrderIsPreserved() {
        Booking booking1 = booking(BOOKING_ID_1, CabinType.STANDARD);
        Booking booking2 = booking(BOOKING_ID_2, CabinType.STANDARD);

        Map<String, List<String>> cabinMap =
                cabins(CABIN_ID_1, CABIN_ID_2);

        List<CabinAttribution> result =
                engine.assign(List.of(booking1, booking2), cabinMap);

        assertEquals(new CabinId(CABIN_ID_1), result.get(0).getCabinId());
        assertEquals(new CabinId(CABIN_ID_2), result.get(1).getCabinId());
    }
}
