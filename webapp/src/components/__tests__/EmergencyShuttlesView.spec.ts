import { mount } from '@vue/test-utils'
import axios from 'axios'
import { describe, expect, it, vi } from 'vitest'
import EmergencyShuttlesView from '@/components/EmergencyShuttlesView.vue'

vi.mock('axios')

const API_RESPONSE_WITH_SHUTTLES = {
    data: {
        cost: 200000,
        emergencyShuttles: [
            {
                type: 'RESCUE_SHIP',
                travelers: [
                    { travelerId: 'T1', travelerName: 'Alice' }
                ],
                crewMembers: [
                    { employeeId: 'E1', crewMemberName: 'Bob' }
                ]
            },
            {
                type: 'STANDARD_SHUTTLE',
                travelers: [
                    { travelerId: 'T2', travelerName: 'Charlie' }
                ],
                crewMembers: []
            }
        ]
    }
}

const API_RESPONSE_EMPTY = {
    data: { cost: 0, emergencyShuttles: [] }
}

const FETCH_BUTTON = '[data-cy=\"fetch-emergency-shuttles-manifest\"]'
const SHUTTLE_ROWS = '[data-cy=\"emergency-shuttle\"]'
const TOTAL_COST = '[data-cy=\"total-cost\"]'

describe('EmergencyShuttlesView.vue', () => {
    it('affiche les navettes et le coût retournés par l’API', async () => {
        ;(axios.get as any).mockResolvedValue(API_RESPONSE_WITH_SHUTTLES)

        const wrapper = mount(EmergencyShuttlesView)

        await wrapper.find(FETCH_BUTTON).trigger('click')

        const shuttles = wrapper.findAll(SHUTTLE_ROWS)
        expect(shuttles).toHaveLength(API_RESPONSE_WITH_SHUTTLES.data.emergencyShuttles.length)
        expect(wrapper.find(TOTAL_COST).text()).toBe(String(API_RESPONSE_WITH_SHUTTLES.data.cost))
    })

    it('rend le bouton rouge quand le manifeste est vide', async () => {
        ;(axios.get as any).mockResolvedValue(API_RESPONSE_EMPTY)

        const wrapper = mount(EmergencyShuttlesView)

        await wrapper.find(FETCH_BUTTON).trigger('click')

        expect(wrapper.find(FETCH_BUTTON).classes()).toContain('red-button')
    })
})
