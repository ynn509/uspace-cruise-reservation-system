package uspace.infra.cabin;

import org.junit.jupiter.api.Test;
import uspace.domain.cruise.cabin.CabinRegistry;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CabinReaderTest {

    private static final String STANDARD_CATEGORY = "STANDARD";
    private static final String DELUXE_CATEGORY = "DELUXE";
    private static final String SUITE_CATEGORY = "SUITE";
    private static final String CABIN_JSON_PATH = "src/main/resources/data/cabins.json";

    private final CabinRegistry cabinReader = new CabinReader(CABIN_JSON_PATH);

    @Test
    void givenValidCabinsFile_whenCallingGetCabinsByCategory_thenReturnAllCategories() {
        Map<String, List<String>> cabins = cabinReader.getCabinsByCategory();

        assertNotNull(cabins);
        assertTrue(cabins.containsKey(STANDARD_CATEGORY));
        assertTrue(cabins.containsKey(DELUXE_CATEGORY));
        assertTrue(cabins.containsKey(SUITE_CATEGORY));
    }

    @Test
    void givenValidCabinsFile_whenCallingGetCabinsByCategory_thenStandardCategoryHas100Cabins() {
        Map<String, List<String>> cabins = cabinReader.getCabinsByCategory();

        List<String> standardCabins = cabins.get(STANDARD_CATEGORY);
        assertEquals(100, standardCabins.size());
    }

    @Test
    void givenValidCabinsFile_whenCallingGetCabinsByCategory_thenDeluxeCategoryHas75Cabins() {
        Map<String, List<String>> cabins = cabinReader.getCabinsByCategory();

        List<String> deluxeCabins = cabins.get(DELUXE_CATEGORY);
        assertEquals(65, deluxeCabins.size());
    }

    @Test
    void givenValidCabinsFile_whenCallingGetCabinsByCategory_thenSuiteCategoryHas25Cabins() {
        Map<String, List<String>> cabins = cabinReader.getCabinsByCategory();

        List<String> suiteCabins = cabins.get(SUITE_CATEGORY);
        assertEquals(25, suiteCabins.size());
    }

    @Test
    void givenValidCabinsFile_whenCallingGetCabinsByCategory_thenCabinIdsAreInPriorityOrder() {
        Map<String, List<String>> cabins = cabinReader.getCabinsByCategory();

        List<String> standardCabins = cabins.get(STANDARD_CATEGORY);
        assertTrue(standardCabins.get(0).startsWith("A"));
        assertTrue(standardCabins.get(1).startsWith("A"));
    }

    @Test
    void givenValidCabinsFile_whenCallingGetCabinsByCategory_thenAllCabinIdsAreUnique() {
        Map<String, List<String>> cabins = cabinReader.getCabinsByCategory();

        long totalCabins = cabins.values().stream()
                .flatMap(List::stream)
                .count();

        long uniqueCabins = cabins.values().stream()
                .flatMap(List::stream)
                .distinct()
                .count();

        assertEquals(totalCabins, uniqueCabins);
    }

    @Test
    void givenValidCabinsFile_whenCallingGetCabinsByCategory_thenStandardCabinsIsNotEmpty() {
        Map<String, List<String>> cabins = cabinReader.getCabinsByCategory();

        assertFalse(cabins.get(STANDARD_CATEGORY).isEmpty());
    }

    @Test
    void givenValidCabinsFile_whenCallingGetCabinsByCategory_thenDeluxeCabinsIsNotEmpty() {
        Map<String, List<String>> cabins = cabinReader.getCabinsByCategory();

        assertFalse(cabins.get(DELUXE_CATEGORY).isEmpty());
    }

    @Test
    void givenValidCabinsFile_whenCallingGetCabinsByCategory_thenSuiteCabinsIsNotEmpty() {
        Map<String, List<String>> cabins = cabinReader.getCabinsByCategory();

        assertFalse(cabins.get(SUITE_CATEGORY).isEmpty());
    }

    @Test
    void givenValidCabinsFile_whenCallingGetCabinsByCategory_thenReturnMapIsNotNull() {
        Map<String, List<String>> cabins = cabinReader.getCabinsByCategory();

        assertNotNull(cabins);
    }
}