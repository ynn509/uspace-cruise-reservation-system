package uspace.application.cruise.cabin;

import jakarta.inject.Inject;
import uspace.api.cruise.cabin.CabinAttributionCriteriaValidator;
import uspace.application.cruise.cabin.dtos.CabinAttributionResponseDto;
import uspace.domain.cruise.Cruise;
import uspace.domain.cruise.CruiseId;
import uspace.domain.cruise.CruiseRepository;
import uspace.domain.cruise.booking.Booking;
import uspace.domain.cruise.cabin.CabinAttributionCriteria;
import uspace.domain.cruise.cabin.CabinAttributionEngine;
import uspace.domain.cruise.cabin.CabinBookingSorter;
import uspace.domain.cruise.cabin.CabinRegistry;
import uspace.domain.cruise.exceptions.CruiseNotFoundException;

import java.util.List;
import java.util.Map;

public class CabinAttributionService {

    private final CruiseRepository cruiseRepository;
    private final CabinRegistry cabinRegistry;
    private final CabinBookingSorter sorter;
    private final CabinAttributionEngine engine;
    private final CabinAttributionAssembler assembler;
    private final CabinAttributionCriteriaValidator validator;

    @Inject
    public CabinAttributionService(CruiseRepository cruiseRepository,
                                   CabinRegistry cabinRegistry,
                                   CabinBookingSorter sorter,
                                   CabinAttributionEngine engine,
                                   CabinAttributionAssembler assembler,
                                   CabinAttributionCriteriaValidator validator) {
        this.cruiseRepository = cruiseRepository;
        this.cabinRegistry = cabinRegistry;
        this.sorter = sorter;
        this.engine = engine;
        this.assembler = assembler;
        this.validator = validator;
    }

    public CabinAttributionResponseDto getAttributions(String cruiseId, String criteriaString) {

        CabinAttributionCriteria criteria = validator.validate(criteriaString);

        Cruise cruise = findCruiseById(cruiseId);


        Map<String, List<String>> cabinsByCategory = cabinRegistry.getCabinsByCategory();

        List<Booking> travelerBookings = extractTravelerBookings(cruise);

        List<Booking> sorted = sorter.sort(travelerBookings, criteria);

        return assembler.toDto(
                assembler.fromDomain(engine.assign(sorted, cabinsByCategory))
        );
    }

    private List<Booking> extractTravelerBookings(Cruise cruise) {
        return cruise.getBookings().stream()
                .filter(b -> b.getTravelers() != null && !b.getTravelers().isEmpty())
                .toList();
    }

    private Cruise findCruiseById(String cruiseId) {
        Cruise cruise = cruiseRepository.findById(new CruiseId(cruiseId));
        if (cruise == null) {
            throw new CruiseNotFoundException();
        }
        return cruise;
    }

}
