package uspace.domain.cruise.cabin;

import uspace.domain.cruise.booking.Booking;

import java.util.List;
import java.util.Map;

public interface CabinAttributionEngine {
    List<CabinAttribution> assign(List<Booking> bookings,
                                  Map<String, List<String>> cabinsByCategory);
}
