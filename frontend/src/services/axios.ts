import axios from 'axios';

const axiosClient = axios.create({
  baseURL:
    'https://vpic.nhtsa.dot.gov/api/vehicles',
    params: {
      format: 'json'
    }
});

export default axiosClient;
