import axios from 'axios';
import { apiUrl } from '../config';

export const setupApi = () => {
	axios.defaults.baseURL = apiUrl;
};
