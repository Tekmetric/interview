import { ROOT_URL } from '../../shared/constants';
import axios from 'axios';

export const getCars = async (searchParams, page) => {
  console.log(`GET ALL /cars?${searchParams.toString()}&page=${page}`);
  const res = await axios.get(`${ROOT_URL}/cars?${searchParams.toString()}&page=${page}`);
  return res.data;
};

export const getCar = async (id) => {
  console.log('GET ONE /cars/' + id);
  const res = await axios.get(`${ROOT_URL}/cars/${id}`);
  return res.data;
};

export const postCar = async (body) => {
  console.log('POST /cars');
  const res = await axios.post(`${ROOT_URL}/cars`, body);
  return res.data;
};

export const patchCar = async (id, body) => {
  console.log('PATCH /cars/' + id);
  const res = await axios.patch(`${ROOT_URL}/cars/${id}`, body);
  return res.data;
};

export const deleteCar = async (id) => {
  console.log('DELETE /cars/' + id);
  const res = await axios.delete(`${ROOT_URL}/cars/${id}`);
  return res.data;
};
