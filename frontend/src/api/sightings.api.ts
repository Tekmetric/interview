import axios from "axios";
import { AddSightingDTO } from "../types/Sighting";
// import * as dotenv from 'dotenv/config';

// const baseUrl = dotenv.config().parsed;

const baseUrl = "http://localhost:3000/api/sighting";

export const fetchAllSightings = () => axios.get(baseUrl + "/list");
  
export const createSighting = (sighting: AddSightingDTO) => axios.post(baseUrl + "/add", sighting);

