package uspace.domain.cruise.cabin;

import uspace.domain.cruise.booking.Booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultCabinAttributionEngine implements CabinAttributionEngine {

    @Override
    public List<CabinAttribution> assign(List<Booking> bookings,
                                         Map<String, List<String>> cabinsByCategory) {

        List<CabinAttribution> result = new ArrayList<>();

        Map<CabinType, List<Booking>> bookingsByType =
                bookings.stream().collect(Collectors.groupingBy(Booking::getCabinType));

        for (Map.Entry<String, List<String>> entry : cabinsByCategory.entrySet()) {

            CabinType type = CabinType.fromString(entry.getKey());

            List<String> cabinIds = entry.getValue();
            List<Booking> bookingsOfType = bookingsByType.getOrDefault(type, List.of());

            for (int i = 0; i < Math.min(cabinIds.size(), bookingsOfType.size()); i++) {
                result.add(new CabinAttribution(
                        new CabinId(cabinIds.get(i)),
                        bookingsOfType.get(i).getId(),
                        type
                ));
            }
        }

        return result;
    }
}
