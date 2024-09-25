import axios from "axios";
import { RedPanda } from "../types/RedPanda";
// import * as dotenv from 'dotenv/config';

// const baseUrl = dotenv.config().parsed;

const baseUrl = "http://localhost:3000/api/redPanda";

export const fetchAllPandas = () => axios.get(baseUrl + "/list");

export const getPandaById = (id: string) => axios.get(baseUrl, { params: { id }});
  
export const createPanda = (panda: RedPanda) => axios.post(baseUrl + "/add", panda);

export const updatePanda = (panda: RedPanda) => axios.put(baseUrl + "/update", panda);

export const deletePanda = (id: string) => axios.delete(baseUrl + "/delete", { params: { id }});

