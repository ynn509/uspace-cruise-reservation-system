package uspace.application.cruise.emergencyShuttle;

import uspace.domain.cruise.CruiseId;
import uspace.domain.cruise.emergencyShuttle.EmergencyShuttleSpecification;
import uspace.domain.cruise.emergencyShuttle.EmergencyShuttleSpecifications;
import uspace.domain.cruise.emergencyShuttle.EmergencyShuttleType;
import uspace.domain.cruise.emergencyShuttle.configuration.EmergencyShuttleConfiguration;
import uspace.domain.cruise.money.Money;
import uspace.UspaceMain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmergencyShuttleConfigurationProperties implements EmergencyShuttleConfiguration {

    private static final int DEFAULT_RESCUE_SHIP_CAPACITY = 50;
    private static final int DEFAULT_STANDARD_SHUTTLE_CAPACITY = 20;
    private static final Money DEFAULT_RESCUE_SHIP_COST = new Money(150_000);
    private static final Money DEFAULT_STANDARD_SHUTTLE_COST = new Money(50_000);
    private static final int DEFAULT_RESCUE_SHIP_LIMIT = 1;
    private static final int DEFAULT_CRUISE_RESCUE_SHIP_LIMIT = 5;

    private final int rescueShipCapacity;
    private final int standardShuttleCapacity;
    private final Money rescueShipCost;
    private final Money standardShuttleCost;
    private final Map<CruiseId, Integer> rescueShipLimits;

    public EmergencyShuttleConfigurationProperties() {
        this(DEFAULT_RESCUE_SHIP_CAPACITY, DEFAULT_STANDARD_SHUTTLE_CAPACITY,
                DEFAULT_RESCUE_SHIP_COST, DEFAULT_STANDARD_SHUTTLE_COST,
                defaultRescueShipLimits());
    }

    public EmergencyShuttleConfigurationProperties(int rescueShipCapacity, int standardShuttleCapacity,
                                                   Money rescueShipCost, Money standardShuttleCost,
                                                   Map<CruiseId, Integer> rescueShipLimits) {
        this.rescueShipCapacity = rescueShipCapacity;
        this.standardShuttleCapacity = standardShuttleCapacity;
        this.rescueShipCost = rescueShipCost;
        this.standardShuttleCost = standardShuttleCost;
        this.rescueShipLimits = rescueShipLimits;
    }

    @Override
    public EmergencyShuttleSpecifications specificationsFor(CruiseId cruiseId) {
        EmergencyShuttleSpecification rescueShipSpecification = new EmergencyShuttleSpecification(
                EmergencyShuttleType.RESCUE_SHIP,
                rescueShipCapacity,
                rescueShipCost,
                rescueShipLimits.getOrDefault(cruiseId, DEFAULT_RESCUE_SHIP_LIMIT)
        );
        EmergencyShuttleSpecification standardShuttleSpecification = new EmergencyShuttleSpecification(
                EmergencyShuttleType.STANDARD_SHUTTLE,
                standardShuttleCapacity,
                standardShuttleCost,
                Integer.MAX_VALUE
        );

        Map<EmergencyShuttleType, EmergencyShuttleSpecification> specificationsByType = Map.of(
                EmergencyShuttleType.RESCUE_SHIP, rescueShipSpecification,
                EmergencyShuttleType.STANDARD_SHUTTLE, standardShuttleSpecification
        );

        return new EmergencyShuttleSpecifications(specificationsByType,
                List.of(EmergencyShuttleType.RESCUE_SHIP, EmergencyShuttleType.STANDARD_SHUTTLE));
    }

    private static Map<CruiseId, Integer> defaultRescueShipLimits() {
        Map<CruiseId, Integer> limits = new HashMap<>();
        limits.put(new CruiseId(UspaceMain.DEFAULT_CRUISE_ID), DEFAULT_CRUISE_RESCUE_SHIP_LIMIT);
        return limits;
    }
}
