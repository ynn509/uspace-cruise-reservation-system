package uspace.domain.cruise.emergencyShuttle;

import uspace.domain.cruise.money.Money;

import java.util.List;

public class EmergencyShuttleManifest {
    private static final Money EMPTY_COST = Money.zero();
    private final List<EmergencyShuttle> shuttles;

    public EmergencyShuttleManifest(List<EmergencyShuttle> shuttles) {
        this.shuttles = shuttles;
    }

    public List<EmergencyShuttle> getShuttles() {
        return List.copyOf(shuttles);
    }

    public Money totalCost() {
        return shuttles.stream()
                .map(EmergencyShuttle::getCost)
                .reduce(EMPTY_COST, Money::add);
    }

    public boolean isEmpty() {
        return shuttles.isEmpty();
    }
}
