package uspace.domain.cruise.cabin;

import uspace.domain.cruise.booking.Booking;

import java.util.List;

public interface CabinBookingSorter {
    List<Booking> sort(List<Booking> bookings, CabinAttributionCriteria criteria);
}

