package uspace.infra.cabin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uspace.domain.cruise.cabin.CabinRegistry;
import uspace.infra.cabin.dtos.CabinFilesDto;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CabinReader implements CabinRegistry {
    private final Logger logger = LoggerFactory.getLogger(CabinReader.class);
    private final String cabinJsonPath;

    public CabinReader(String cabinJsonPath) {
        this.cabinJsonPath = cabinJsonPath;
    }

    @Override
    public Map<String, List<String>> getCabinsByCategory() {
        List<uspace.infra.cabin.dtos.CabinDto> cabinDtos = readCabinJson();

        return cabinDtos.stream()
                .collect(Collectors.toMap(
                        cabin -> cabin.category,
                        cabin -> cabin.ids,
                        (existing, replacement) -> existing
                ));
    }

    private List<uspace.infra.cabin.dtos.CabinDto> readCabinJson() {
        String cabinJsonStr = null;
        try {
            FileInputStream stream = new FileInputStream(cabinJsonPath);
            cabinJsonStr = new String(stream.readAllBytes());
        } catch (IOException e) {
            logger.error("Error while trying to read cabin json file " + cabinJsonPath, e);
            throw new RuntimeException(e);
        }

        try {
            CabinFilesDto cabinFilesDto = new ObjectMapper().readValue(cabinJsonStr, CabinFilesDto.class);
            return cabinFilesDto.cabins;
        } catch (JsonProcessingException e) {
            logger.error("Error while trying to parse cabin json file " + cabinJsonPath, e);
            throw new RuntimeException(e);
        }
    }
}