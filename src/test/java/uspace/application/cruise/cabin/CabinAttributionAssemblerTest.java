package uspace.application.cruise.cabin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uspace.application.cruise.cabin.dtos.CabinAttributionDto;
import uspace.application.cruise.cabin.dtos.CabinAttributionResponseDto;
import uspace.domain.cruise.booking.BookingId;
import uspace.domain.cruise.cabin.CabinAttribution;
import uspace.domain.cruise.cabin.CabinId;
import uspace.domain.cruise.cabin.CabinType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CabinAttributionAssemblerTest {

    private static final String CABIN_ID_1 = "A1";
    private static final String CABIN_ID_2 = "B1";

    private static final String BOOKING_ID_1 = "BKG1";
    private static final String BOOKING_ID_2 = "BKG2";

    private static final CabinType TYPE_STANDARD = CabinType.STANDARD;
    private static final CabinType TYPE_DELUXE = CabinType.DELUXE;

    private CabinAttributionAssembler assembler;

    @BeforeEach
    void setup() {
        assembler = new CabinAttributionAssembler();
    }

    private CabinAttribution attribution(CabinId cabinId, String bookingId, CabinType type) {
        return new CabinAttribution(cabinId, new BookingId(bookingId), type);
    }

    @Test
    void givenDomainAttributions_whenFromDomain_thenDtosAreCreatedCorrectly() {
        CabinAttribution a1 = attribution(new CabinId(CABIN_ID_1), BOOKING_ID_1, TYPE_STANDARD);
        CabinAttribution a2 = attribution(new CabinId(CABIN_ID_2), BOOKING_ID_2, TYPE_DELUXE);

        List<CabinAttributionDto> dtos = assembler.fromDomain(List.of(a1, a2));

        assertEquals(2, dtos.size());

        CabinAttributionDto dto1 = dtos.get(0);
        assertEquals(CABIN_ID_1, dto1.cabinId);
        assertEquals(BOOKING_ID_1, dto1.bookingId);
        assertEquals(TYPE_STANDARD.name(), dto1.category);

        CabinAttributionDto dto2 = dtos.get(1);
        assertEquals(CABIN_ID_2, dto2.cabinId);
        assertEquals(BOOKING_ID_2, dto2.bookingId);
        assertEquals(TYPE_DELUXE.name(), dto2.category);
    }

    @Test
    void givenEmptyDomainList_whenFromDomain_thenReturnEmptyList() {
        List<CabinAttributionDto> dtos = assembler.fromDomain(List.of());
        assertTrue(dtos.isEmpty());
    }

    @Test
    void givenDtoList_whenToDto_thenResponseContainsSameList() {
        CabinAttributionDto dto1 = new CabinAttributionDto(CABIN_ID_1, BOOKING_ID_1, TYPE_STANDARD.name());
        CabinAttributionDto dto2 = new CabinAttributionDto(CABIN_ID_2, BOOKING_ID_2, TYPE_DELUXE.name());

        CabinAttributionResponseDto response = assembler.toDto(List.of(dto1, dto2));

        assertEquals(2, response.cabins.size());
        assertEquals(dto1, response.cabins.get(0));
        assertEquals(dto2, response.cabins.get(1));
    }

    @Test
    void givenEmptyList_whenToDto_thenResponseContainsEmptyList() {
        CabinAttributionResponseDto response = assembler.toDto(List.of());
        assertTrue(response.cabins.isEmpty());
    }
}
