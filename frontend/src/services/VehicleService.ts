import axios from 'axios';
import headers from './HeaderService';

const API_URL = 'http://localhost:8080/api/v1/vehicles/';

class VehicleService {
    getVehicles(
        pageNumber: number = 0,
        pageSize: number = 10,
        sortField: string = 'createdAt',
        sortOrder: string = 'ASC'
    ) {
        const params = { pageNumber, pageSize, sortField, sortOrder };
        return axios.get(API_URL, { params: params, headers: headers() });
    }

    getVehicle(id: number) {
        return axios.get(API_URL + id, { headers: headers() });
    }

    createVehicle(
        licensePlate: string,
        brand: string,
        model: string,
        registrationYear: number,
        cost: number
    ) {
        const data = { licensePlate, brand, model, registrationYear, cost };
        return axios.post(API_URL, data, { headers: headers() });
    }

    updateVehicle(
        id: number,
        licensePlate: string,
        brand: string,
        model: string,
        registrationYear: number,
        cost: number
    ) {
        const data = { licensePlate, brand, model, registrationYear, cost };
        return axios.put(API_URL + id, data, { headers: headers() });
    }

    deleteVehicle(id: number) {
        return axios.delete(API_URL + id, { headers: headers() });
    }
}

export default new VehicleService();