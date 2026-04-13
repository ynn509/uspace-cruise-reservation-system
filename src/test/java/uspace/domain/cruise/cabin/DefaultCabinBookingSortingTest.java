package uspace.domain.cruise.cabin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uspace.domain.cruise.booking.Booking;
import uspace.domain.cruise.booking.BookingId;
import uspace.domain.cruise.booking.invoice.Invoice;
import uspace.domain.cruise.booking.traveler.Traveler;
import uspace.domain.cruise.dateTime.CruiseDateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DefaultCabinBookingSortingTest {

    private static final CabinType ANY_CABIN = CabinType.STANDARD;
    private static final Invoice ANY_INVOICE = new Invoice();

    private static final String BOOKING_ID_1 = "1";
    private static final String BOOKING_ID_2 = "2";
    private static final String BOOKING_ID_3 = "3";

    private static final CruiseDateTime DATE_EARLIEST =
            new CruiseDateTime(LocalDateTime.parse("2025-01-01T10:00:00"));
    private static final CruiseDateTime DATE_MIDDLE =
            new CruiseDateTime(LocalDateTime.parse("2025-01-02T10:00:00"));
    private static final CruiseDateTime DATE_LATEST =
            new CruiseDateTime(LocalDateTime.parse("2025-01-03T10:00:00"));

    private static final int TRAVELER_COUNT_ZERO = 0;
    private static final int TRAVELER_COUNT_ONE = 1;
    private static final int TRAVELER_COUNT_TWO = 2;
    private static final int TRAVELER_COUNT_THREE = 3;

    private CabinBookingSorter sorter;

    @BeforeEach
    void setup() {
        sorter = new DefaultCabinBookingSorting();
    }

    private Booking booking(String id, CruiseDateTime date, int travelerCount) {
        List<Traveler> travelers = new ArrayList<>();

        for (int i = 0; i < travelerCount; i++) {
            Traveler travelerMock = mock(Traveler.class);
            travelers.add(travelerMock);
        }

        return new Booking(
                new BookingId(id),
                travelers,
                ANY_CABIN,
                date,
                ANY_INVOICE
        );
    }

    @Test
    void givenThreeBookings_whenSortingByDate_thenBookingsAreChronologicallyOrdered() {
        Booking b1 = booking(BOOKING_ID_1, DATE_LATEST, TRAVELER_COUNT_ONE);
        Booking b2 = booking(BOOKING_ID_2, DATE_EARLIEST, TRAVELER_COUNT_ONE);
        Booking b3 = booking(BOOKING_ID_3, DATE_MIDDLE, TRAVELER_COUNT_ONE);

        List<Booking> result =
                sorter.sort(List.of(b1, b2, b3), CabinAttributionCriteria.BOOKING_DATE_TIME);

        assertEquals(List.of(b2, b3, b1), result);
    }

    @Test
    void givenThreeBookings_whenSortingByTravelers_thenBookingsAreOrderedByDescendingTravelerCount() {
        Booking b1 = booking(BOOKING_ID_1, DATE_EARLIEST, TRAVELER_COUNT_ONE);
        Booking b2 = booking(BOOKING_ID_2, DATE_MIDDLE, TRAVELER_COUNT_THREE);
        Booking b3 = booking(BOOKING_ID_3, DATE_LATEST, TRAVELER_COUNT_TWO);

        List<Booking> result =
                sorter.sort(List.of(b1, b2, b3), CabinAttributionCriteria.TRAVELERS);

        assertEquals(List.of(b2, b3, b1), result);
    }

    @Test
    void givenBookingsWithEqualTravelerCount_whenSortingByTravelers_thenEarlierDateComesFirst() {
        Booking b1 = booking(BOOKING_ID_1, DATE_LATEST, TRAVELER_COUNT_TWO);
        Booking b2 = booking(BOOKING_ID_2, DATE_EARLIEST, TRAVELER_COUNT_TWO);

        List<Booking> result =
                sorter.sort(List.of(b1, b2), CabinAttributionCriteria.TRAVELERS);

        assertEquals(List.of(b2, b1), result);
    }

    @Test
    void givenBookingsWithZeroTravelers_whenSortingByTravelers_thenBookingsAreStillSortedCorrectly() {
        Booking b1 = booking(BOOKING_ID_1, DATE_MIDDLE, TRAVELER_COUNT_ZERO);
        Booking b2 = booking(BOOKING_ID_2, DATE_EARLIEST, TRAVELER_COUNT_TWO);

        List<Booking> result =
                sorter.sort(List.of(b1, b2), CabinAttributionCriteria.TRAVELERS);

        assertEquals(List.of(b2, b1), result);
    }

    @Test
    void givenBookingsWithSameTravelerCountAndSameDate_whenSortingByTravelers_thenOriginalOrderIsPreserved() {
        Booking b1 = booking(BOOKING_ID_1, DATE_EARLIEST, TRAVELER_COUNT_TWO);
        Booking b2 = booking(BOOKING_ID_2, DATE_EARLIEST, TRAVELER_COUNT_TWO);

        List<Booking> result =
                sorter.sort(List.of(b1, b2), CabinAttributionCriteria.TRAVELERS);

        assertEquals(List.of(b1, b2), result);
    }

    @Test
    void givenOneBooking_whenSorting_thenSameBookingIsReturned() {
        Booking b1 = booking(BOOKING_ID_1, DATE_EARLIEST, TRAVELER_COUNT_ONE);

        List<Booking> result =
                sorter.sort(List.of(b1), CabinAttributionCriteria.BOOKING_DATE_TIME);

        assertEquals(List.of(b1), result);
    }

    @Test
    void givenEmptyBookingList_whenSorting_thenEmptyListIsReturned() {
        List<Booking> result =
                sorter.sort(List.of(), CabinAttributionCriteria.BOOKING_DATE_TIME);

        assertTrue(result.isEmpty());
    }
}