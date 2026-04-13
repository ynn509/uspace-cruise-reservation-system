const CRUISE_PATH = '/cruises/JUPITER_MOONS_EXPLORATION_2085/cabins*';

const CRITERIA_BOOKING_DATE = 'bookingDateTime';
const CRITERIA_TRAVELERS = 'travelers';

const FETCH_BUTTON = '[data-cy=fetch-cabin-attribution]';
const ROW_SELECTOR = '[data-cy=cabin-attribution-details]';
const SELECT_CRITERIA = '#criteria';


const CABINS_BY_DATE = {
    cabins: [
        { cabinId: 'CABIN_1', category: 'STANDARD', bookingId: 'BOOKING_1' },
        { cabinId: 'CABIN_2', category: 'DELUXE', bookingId: 'BOOKING_2' }
    ]
};

const CABINS_BY_TRAVELERS = {
    cabins: [
        { cabinId: 'CABIN_A', category: 'STANDARD', bookingId: 'BOOKING_A' },
        { cabinId: 'CABIN_B', category: 'STANDARD', bookingId: 'BOOKING_B' }
    ]
};

describe('Cabin Attribution - bookingDateTime', () => {

    it('affiche les cabines retournées par l’API (happy path)', () => {

        cy.intercept('GET', CRUISE_PATH, {
            statusCode: 200,
            body: CABINS_BY_DATE
        }).as('getCabinsByDate');

        cy.visit('/');

        cy.get(SELECT_CRITERIA).select(CRITERIA_BOOKING_DATE);
        cy.get(FETCH_BUTTON).click();

        cy.wait('@getCabinsByDate');

        cy.get(ROW_SELECTOR).should('have.length', CABINS_BY_DATE.cabins.length);
    });
});


describe('Cabin Attribution - travelers', () => {

    it('affiche les cabines retournées par l’API pour le critère travelers (happy path)', () => {

        cy.intercept('GET', CRUISE_PATH, {
            statusCode: 200,
            body: CABINS_BY_TRAVELERS
        }).as('getCabinsTravelers');

        cy.visit('/');

        cy.get(SELECT_CRITERIA).select(CRITERIA_TRAVELERS);
        cy.get(FETCH_BUTTON).click();

        cy.wait('@getCabinsTravelers');

        cy.get(ROW_SELECTOR).should('have.length', CABINS_BY_TRAVELERS.cabins.length);
    });
});
