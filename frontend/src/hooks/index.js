import {useState} from "react";
import axios from "axios";

export const useGetRequest = () => {
    const [loading, setLoading] = useState(false);

    const performGet = async (url, params) => {
        setLoading(true);
        let response;
        try {
            response = await axios.get(url, params);
        } catch (error) {
            console.error(error.message);
        }
        setLoading(false);

        return {
            data: response.data,
            status: response.status
        };
    }

    return {
        loading: loading,
        performGet: performGet
    }

}
