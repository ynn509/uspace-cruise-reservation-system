package uspace.api.cruise.cabin;

import uspace.domain.cruise.cabin.CabinAttributionCriteria;
import uspace.domain.cruise.cabin.exceptions.InvalidCabinAttributionCriteriaException;

public class CabinAttributionCriteriaValidator {

    public CabinAttributionCriteria validate(String value) {
        CabinAttributionCriteria criteria = CabinAttributionCriteria.fromString(value);

        if (criteria == null) {
            throw new InvalidCabinAttributionCriteriaException();
        }

        return criteria;
    }
}
