package uspace.domain.cruise.emergencyShuttle;

import uspace.domain.cruise.money.Money;

public class EmergencyShuttleSpecification {
    private final EmergencyShuttleType type;
    private final int capacity;
    private final Money cost;
    private final int maximumCount;

    public EmergencyShuttleSpecification(EmergencyShuttleType type, int capacity, Money cost, int maximumCount) {
        this.type = type;
        this.capacity = capacity;
        this.cost = cost;
        this.maximumCount = maximumCount;
    }

    public EmergencyShuttleType getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public Money getCost() {
        return cost;
    }

    public int getMaximumCount() {
        return maximumCount;
    }
}
