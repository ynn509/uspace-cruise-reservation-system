package uspace.multiComponents.glue;

import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Quand;
import uspace.UspaceMain;
import uspace.application.cruise.emergencyShuttle.dtos.EmergencyShuttleManifestDto;
import uspace.application.cruise.emergencyShuttle.dtos.EmergencyShuttleDto;
import uspace.multiComponents.util.APIHelper;
import uspace.multiComponents.util.ResponseMemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmergencyShuttleStepDefinition {
    private static final String CRUISE_ID = UspaceMain.DEFAULT_CRUISE_ID;
    private static final String DEFAULT_HOME_PLANET = "Terre";
    private static final String DEFAULT_BOOKING_DATE_TIME = "2084-01-25T12:00";
    private static final String TRAVELER_NAME = "Traveler";
    private static final String STANDARD_CABIN_TYPE = "STANDARD";
    private static final String ACCOUNT_PREFIX = "emergency-account-";
    private static final String EMPLOYEE_ID_TEMPLATE = "EMP%03d";
    private static final String CREW_NAME_PREFIX = "Crew ";
    private static final String RESCUE_SHIP_TYPE = "RESCUE_SHIP";
    private static final String STANDARD_SHUTTLE_TYPE = "STANDARD_SHUTTLE";

    @Etantdonné("une croisière avec {int} voyageurs et {int} membres d'équipage")
    public void uneCroisiereAvecVoyageursEtMembresDEquipage(int travelerCount, int crewCount) {
        String accountUsername = ACCOUNT_PREFIX + UUID.randomUUID();
        APIHelper.createAccount(accountUsername, DEFAULT_HOME_PLANET);
        List<String> travelerNames = new ArrayList<>(Collections.nCopies(travelerCount, TRAVELER_NAME));
        APIHelper.createBooking(CRUISE_ID, accountUsername, STANDARD_CABIN_TYPE, DEFAULT_BOOKING_DATE_TIME, travelerNames, List.of(), List.of());

        for (int i = 0; i < crewCount; i++) {
            APIHelper.addCrewMember(CRUISE_ID, String.format(EMPLOYEE_ID_TEMPLATE, i), CREW_NAME_PREFIX + i);
        }
    }

    @Quand("on récupère le manifeste des navettes d'urgence")
    public void onRecupereLeManifesteDesNavettesDUrgence() {
        ResponseMemory.setResponse(APIHelper.getEmergencyShuttleManifest(CRUISE_ID));
    }

    @Quand("on récupère le manifeste des navettes d'urgence de la croisière {string}")
    public void onRecupereLeManifesteDesNavettesDUrgenceDeLaCroisiere(String cruiseId) {
        ResponseMemory.setResponse(APIHelper.getEmergencyShuttleManifest(cruiseId));
    }

    @Alors("le manifeste contient {int} navettes de sauvetage et {int} navette standard")
    @Alors("le manifeste contient {int} navettes de sauvetage et {int} navettes standard")
    public void leManifesteContientNavettesParType(int expectedRescueShips, int expectedStandardShuttles) {
        EmergencyShuttleManifestDto manifestDto = ResponseMemory.getResponse().as(EmergencyShuttleManifestDto.class);
        long rescueShips = manifestDto.emergencyShuttles.stream().filter(shuttle -> shuttle.type.equals(RESCUE_SHIP_TYPE)).count();
        long standardShuttles = manifestDto.emergencyShuttles.stream().filter(shuttle -> shuttle.type.equals(STANDARD_SHUTTLE_TYPE)).count();

        assertEquals(expectedRescueShips, rescueShips);
        assertEquals(expectedStandardShuttles, standardShuttles);
    }

    @Alors("les navettes de sauvetage sont prioritaires puis les navettes standard")
    public void lesNavettesDeSauvetageSontPrioritairesPuisLesNavettesStandard() {
        EmergencyShuttleManifestDto manifestDto = ResponseMemory.getResponse().as(EmergencyShuttleManifestDto.class);

        boolean standardSeen = false;
        boolean rescueAfterStandard = false;
        for (EmergencyShuttleDto shuttle : manifestDto.emergencyShuttles) {
            if (STANDARD_SHUTTLE_TYPE.equals(shuttle.type)) {
                standardSeen = true;
            } else if (standardSeen) {
                rescueAfterStandard = true;
                break;
            }
        }

        assertFalse(rescueAfterStandard);
    }

    @Alors("aucune navette vide n'est renvoyée")
    public void aucuneNavetteVideNEstRenvoyee() {
        EmergencyShuttleManifestDto manifestDto = ResponseMemory.getResponse().as(EmergencyShuttleManifestDto.class);
        assertTrue(manifestDto.emergencyShuttles.stream()
                .allMatch(shuttle -> shuttle.travelers.size() + shuttle.crewMembers.size() > 0));
    }

    @Alors("le coût total des navettes est de {int}")
    public void leCoutTotalDesNavettesEstDe(int expectedCost) {
        EmergencyShuttleManifestDto manifestDto = ResponseMemory.getResponse().as(EmergencyShuttleManifestDto.class);
        assertEquals(expectedCost, manifestDto.cost);
    }

    @Alors("le manifeste est vide")
    public void leManifesteEstVide() {
        EmergencyShuttleManifestDto manifestDto = ResponseMemory.getResponse().as(EmergencyShuttleManifestDto.class);
        assertEquals(0, manifestDto.emergencyShuttles.size());
        assertEquals(0, manifestDto.cost);
    }
}
