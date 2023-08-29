import {api} from "./AxiosConfiguration";

export const TournamentAPI = {
    /**
     * Get all tournaments available
     * @returns {Promise<any>}
     */
    getAll: async function () {
        const response = await api.request({
            url: "/api/tournaments",
            method: "GET"
        })

        return response.data
    },
}