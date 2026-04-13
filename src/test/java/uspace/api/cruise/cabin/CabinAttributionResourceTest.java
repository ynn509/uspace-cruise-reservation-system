package uspace.api.cruise.cabin;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uspace.application.cruise.cabin.CabinAttributionService;
import uspace.application.cruise.cabin.dtos.CabinAttributionDto;
import uspace.application.cruise.cabin.dtos.CabinAttributionResponseDto;
import uspace.domain.cruise.CruiseId;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CabinAttributionResourceTest {

    private static final String ANY_CRUISE_ID = "CRUISE_TEST_ID";
    private static final CruiseId ANY_CRUISE_ID_OBJECT = new CruiseId(ANY_CRUISE_ID);

    private static final String CRITERIA_BOOKING = "bookingDateTime";

    private static final String CABIN_ID = "CABIN_001";
    private static final String BOOKING_ID = "BOOKING_001";
    private static final String CABIN_CATEGORY = "STANDARD";

    private CabinAttributionService service;
    private CabinAttributionResource resource;

    @BeforeEach
    void setup() {
        service = mock(CabinAttributionService.class);
        resource = new CabinAttributionResource(service);
    }

    @Test
    void whenServiceReturnsNonEmptyList_thenResponseContainsBody() {

        CabinAttributionDto dto =
                new CabinAttributionDto(CABIN_ID, BOOKING_ID, CABIN_CATEGORY);

        CabinAttributionResponseDto responseDto =
                new CabinAttributionResponseDto(List.of(dto));

        when(service.getAttributions(ANY_CRUISE_ID, CRITERIA_BOOKING))
                .thenReturn(responseDto);

        Response response = resource.getCabinAttributions(ANY_CRUISE_ID, CRITERIA_BOOKING);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        CabinAttributionResponseDto body =
                (CabinAttributionResponseDto) response.getEntity();

        assertNotNull(body);
        assertEquals(1, body.cabins.size());
        assertEquals(dto, body.cabins.get(0));
    }

    @Test
    void whenServiceReturnsEmptyList_thenResponseIsOkWithoutBody() {

        CabinAttributionResponseDto emptyResponse =
                new CabinAttributionResponseDto(Collections.emptyList());

        when(service.getAttributions(ANY_CRUISE_ID, CRITERIA_BOOKING))
                .thenReturn(emptyResponse);

        Response response = resource.getCabinAttributions(ANY_CRUISE_ID, CRITERIA_BOOKING);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNull(response.getEntity());
    }

    @Test
    void whenInvoked_thenServiceReceivesCorrectParameters() {

        CabinAttributionResponseDto emptyResponse =
                new CabinAttributionResponseDto(Collections.emptyList());

        when(service.getAttributions(ANY_CRUISE_ID, CRITERIA_BOOKING))
                .thenReturn(emptyResponse);

        resource.getCabinAttributions(ANY_CRUISE_ID, CRITERIA_BOOKING);

        verify(service).getAttributions(ANY_CRUISE_ID, CRITERIA_BOOKING);
    }
}
