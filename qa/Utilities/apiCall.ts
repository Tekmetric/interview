import axios from 'axios';

export async function makeApiCall(url: string , method: string , data?: unknown, headers: Record<string, string> = {}) {

    const options = {
        method,
        url,
        headers: {
            'Content-Type': 'application/json',
            ...headers,
        },
        data: data ?? undefined,
    };

    try {
        console.log('*** Request:', { method: options.method, url: options.url, headers: options.headers });
        const response = await axios(options);
        console.log('*** Response:', { status: response.status, statusText: response.statusText });
        return response;
    } catch (error) {
        if(error.response) {
            console.log('*** Error response:', {
                status: error.response.status,
                statusText: error.response.statusText,
                data: error.response.data,
                url: error.response.config?.url,
                method: error.response.config?.method,
            });
            return error.response;
        }else {
            console.error('Error making request:', error);
            throw error;
        }
    }
}