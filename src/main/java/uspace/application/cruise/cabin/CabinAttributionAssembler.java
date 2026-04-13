package uspace.application.cruise.cabin;

import uspace.application.cruise.cabin.dtos.CabinAttributionDto;
import uspace.application.cruise.cabin.dtos.CabinAttributionResponseDto;
import uspace.domain.cruise.cabin.CabinAttribution;

import java.util.List;

public class CabinAttributionAssembler {

    public List<CabinAttributionDto> fromDomain(List<CabinAttribution> attributions) {
        return attributions.stream()
                .map(a -> new CabinAttributionDto(
                        a.getCabinId().getValue(),
                        a.getBookingId().toString(),
                        a.getCabinType().name()
                ))
                .toList();
    }

    public CabinAttributionResponseDto toDto(List<CabinAttributionDto> dtos) {
        return new CabinAttributionResponseDto(dtos);
    }
}
