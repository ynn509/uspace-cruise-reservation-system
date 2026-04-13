package uspace.application.cruise.cabin.dtos;

import java.util.List;

public class CabinAttributionResponseDto {
    public List<CabinAttributionDto> cabins;

    public CabinAttributionResponseDto(List<CabinAttributionDto> cabins) {
        this.cabins = cabins;
    }
}