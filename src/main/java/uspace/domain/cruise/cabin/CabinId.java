package uspace.domain.cruise.cabin;

import java.util.Objects;

public class CabinId {

    private final String value;

    public CabinId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CabinId cannot be null or empty");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CabinId)) return false;
        CabinId cabinId = (CabinId) o;
        return value.equals(cabinId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
