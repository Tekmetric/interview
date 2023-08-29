import axios from "axios"

export const api = axios.create({
    withCredentials: true,
    baseURL: "http://localhost:8085/"
})

/**
 * Defining a custom error handler for all APIs
 * @param error
 * @returns {Promise<never>}
 */
const errorHandler = (error) => {
    const statusCode = error.response?.status

    if (statusCode && statusCode !== 401) {
        console.error(error)
    }

    return Promise.reject(error)
}

/**
 * registering the custom error handler to the "api" axios instance
 */
api.interceptors.response.use(undefined, (error) => {
    return errorHandler(error)
})