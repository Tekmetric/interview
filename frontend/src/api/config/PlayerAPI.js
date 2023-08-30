import {api} from "./AxiosConfiguration";

export const PlayerAPI = {
    /**
     * Get all players
     * @returns {Promise<any>}
     */
    getAll: async function () {
        const response = await api.request({
            url: "/api/players",
            method: "GET"
        })

        return response.data
    },
    /**
     * Save a new player
     * @param player
     * @returns {Promise<any>}
     */
    save: async function (player) {
        const response = await api.request({
            url: `/api/players`,
            method: "POST",
            data: player
        });

        return response.data
    },
    /**
     * Delete selected player by id
     * @param playerId
     * @returns {Promise<void>}
     */
    delete: async function (playerId) {
        await api.request({
            url: `/api/players/${playerId}`,
            method: "DELETE"
        })
    }
}