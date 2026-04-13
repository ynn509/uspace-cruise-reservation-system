package uspace.api.cruise.cabin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uspace.domain.cruise.cabin.CabinAttributionCriteria;
import uspace.domain.cruise.cabin.exceptions.InvalidCabinAttributionCriteriaException;


import static org.junit.jupiter.api.Assertions.*;

class CabinAttributionCriteriaValidatorTest {

    private static final String VALID_BOOKING_DATE_TIME = "bookingDateTime";
    private static final String VALID_TRAVELERS = "travelers";
    private static final String INVALID = "invalid";

    private CabinAttributionCriteriaValidator validator;

    @BeforeEach
    void setup() {
        validator = new CabinAttributionCriteriaValidator();
    }

    @Test
    void givenValidBookingDateTime_whenValidate_thenReturnEnum() {
        CabinAttributionCriteria result = validator.validate(VALID_BOOKING_DATE_TIME);

        assertEquals(CabinAttributionCriteria.BOOKING_DATE_TIME, result);
    }

    @Test
    void givenValidTravelersCriteria_whenValidate_thenReturnEnum() {
        CabinAttributionCriteria result = validator.validate(VALID_TRAVELERS);

        assertEquals(CabinAttributionCriteria.TRAVELERS, result);
    }

    @Test
    void givenInvalidString_whenValidate_thenThrowException() {
        assertThrows(InvalidCabinAttributionCriteriaException.class,
                () -> validator.validate(INVALID));
    }
    @Test
    void givenNullCriteria_whenValidate_thenThrowException() {
        assertThrows(InvalidCabinAttributionCriteriaException.class,
                () -> validator.validate(null));
    }

    @Test
    void givenEmptyString_whenValidate_thenThrowException() {
        assertThrows(InvalidCabinAttributionCriteriaException.class,
                () -> validator.validate(""));
    }
}
