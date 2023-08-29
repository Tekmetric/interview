import {api} from "./AxiosConfiguration";

export const RacquetsAPI = {
    /**
     * Get all racquets available
     * @returns {Promise<any>}
     */
    getAll: async function () {
        const response = await api.request({
            url: "/api/racquets",
            method: "GET"
        })

        return response.data
    }
}