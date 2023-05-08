import { ROOT_URL } from '../../shared/constants';
import axios from 'axios';

export const getCars = async (searchParams, page) => {
  const res = await axios.get(`${ROOT_URL}/cars?${searchParams.toString()}&page=${page}`);
  return res.data;
};

export const getBrands = async () => {
  const res = await axios.get(`${ROOT_URL}/cars/brands`);
  return res.data;
};

export const getColors = async () => {
  const res = await axios.get(`${ROOT_URL}/cars/colors`);
  return res.data;
};

export const getCar = async (id) => {
  const res = await axios.get(`${ROOT_URL}/cars/${id}`);
  return res.data;
};

export const postCar = async (body) => {
  const res = await axios.post(`${ROOT_URL}/cars`, body);
  return res.data;
};

export const patchCar = async ({ carId: id, values: body }) => {
  const res = await axios.patch(`${ROOT_URL}/cars/${id}`, body);
  return res.data;
};

export const deleteCar = async (id) => {
  const res = await axios.delete(`${ROOT_URL}/cars/${id}`);
  return res.data;
};
