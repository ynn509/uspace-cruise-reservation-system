const MANIFEST_PATH = '/cruises/JUPITER_MOONS_EXPLORATION_2085/emergencyShuttles';
const FETCH_BUTTON = '[data-cy=fetch-emergency-shuttles-manifest]';
const SHUTTLE_ROWS = '[data-cy=emergency-shuttle]';
const TOTAL_COST = '[data-cy=total-cost]';

const MANIFEST_RESPONSE = {
    cost: 800000,
    emergencyShuttles: [
        {
            type: 'RESCUE_SHIP',
            travelers: [{ travelerId: 'T1', travelerName: 'Alice' }],
            crewMembers: [{ employeeId: 'E1', crewMemberName: 'Bob' }]
        }
    ]
};

const EMPTY_MANIFEST_RESPONSE = {
    cost: 0,
    emergencyShuttles: []
};

describe('Emergency Shuttles Manifest', () => {
    it('affiche la liste des navettes et le coût total quand le manifeste est non vide', () => {
        cy.intercept('GET', MANIFEST_PATH, {
            statusCode: 200,
            body: MANIFEST_RESPONSE
        }).as('getManifest');

        cy.visit('/');

        cy.get(FETCH_BUTTON).click();
        cy.wait('@getManifest');

        cy.get(SHUTTLE_ROWS).should('have.length', MANIFEST_RESPONSE.emergencyShuttles.length);
        cy.get(TOTAL_COST).should('have.text', `${MANIFEST_RESPONSE.cost}`);
    });

    it('rend le bouton rouge quand le manifeste obtenu est vide', () => {
        cy.intercept('GET', MANIFEST_PATH, {
            statusCode: 200,
            body: EMPTY_MANIFEST_RESPONSE
        }).as('getEmptyManifest');

        cy.visit('/');

        cy.get(FETCH_BUTTON).click();
        cy.wait('@getEmptyManifest');

        cy.get(FETCH_BUTTON).should('have.class', 'red-button');
    });
});
