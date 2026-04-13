package uspace.domain.cruise.cabin;

import uspace.domain.cruise.booking.Booking;

import java.util.Comparator;
import java.util.List;


public class DefaultCabinBookingSorting implements CabinBookingSorter {

    @Override
    public List<Booking> sort(List<Booking> bookings, CabinAttributionCriteria criteria) {

        return switch (criteria) {

            case BOOKING_DATE_TIME -> bookings.stream()
                    .sorted(Comparator.comparing(
                            b -> b.getBookingDateTime().toString()
                    ))
                    .toList();

            case TRAVELERS -> bookings.stream()
                    .sorted(
                            Comparator
                                    .comparingInt((Booking b) -> b.getTravelers().size()).reversed()
                                    .thenComparing(b -> b.getBookingDateTime().toString())
                    )
                    .toList();
        };
    }
}
