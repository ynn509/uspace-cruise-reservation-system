import { mount } from '@vue/test-utils'
import axios from 'axios'
import { describe, it, expect, vi } from 'vitest'
import CabinAttributions from '@/components/CabinsView.vue'

vi.mock('axios')


const API_RESPONSE_TWO_CABINS = {
    data: {
        cabins: [
            { cabinId: "CABIN_1", category: "DELUXE", bookingId: "BOOKING_1" },
            { cabinId: "CABIN_2", category: "STANDARD", bookingId: "BOOKING_2" }
        ]
    }
}

const API_RESPONSE_EMPTY = {
    data: { cabins: [] }
}

const ERROR_RESPONSE = new Error("API_ERROR")

const CRITERIA_TRAVELERS = "travelers"
const CRITERIA_BOOKING_DATE = "bookingDateTime"

const FETCH_BUTTON = '[data-cy="fetch-cabin-attribution"]'
const SELECT_CRITERIA = '#criteria'


describe('CabinAttributions.vue', () => {

    it('met à jour cabins après une réponse API valide', async () => {
        (axios.get as any).mockResolvedValue(API_RESPONSE_TWO_CABINS)

        const wrapper = mount(CabinAttributions)

        await wrapper.find(FETCH_BUTTON).trigger('click')

        const cabins = (wrapper.vm as any).cabins
        expect(cabins).toHaveLength(API_RESPONSE_TWO_CABINS.data.cabins.length)


        expect(cabins[0]).toEqual(API_RESPONSE_TWO_CABINS.data.cabins[0])
    })

    it('gère une réponse vide sans erreur', async () => {
        (axios.get as any).mockResolvedValue(API_RESPONSE_EMPTY)

        const wrapper = mount(CabinAttributions)

        await wrapper.find(FETCH_BUTTON).trigger('click')

        const cabins = (wrapper.vm as any).cabins
        expect(cabins).toHaveLength(0)
    })

    it('gère une erreur API en laissant cabins vide', async () => {
        (axios.get as any).mockRejectedValue(ERROR_RESPONSE)

        const wrapper = mount(CabinAttributions)

        await wrapper.find(FETCH_BUTTON).trigger('click')

        const cabins = (wrapper.vm as any).cabins
        expect(cabins).toHaveLength(0)
    })

    it('met à jour le critère sélectionné', async () => {
        const wrapper = mount(CabinAttributions)

        const select = wrapper.find(SELECT_CRITERIA)

        await select.setValue(CRITERIA_TRAVELERS)
        expect((wrapper.vm as any).criteria).toBe(CRITERIA_TRAVELERS)

        await select.setValue(CRITERIA_BOOKING_DATE)
        expect((wrapper.vm as any).criteria).toBe(CRITERIA_BOOKING_DATE)
    })
})
