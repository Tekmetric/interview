import type { METRO_COLORS } from '@constants/wmata';

export type RailLine = keyof typeof METRO_COLORS;

export interface AIMPredictionTrainInfo {
  Car: string | null;
  Destination: string;
  DestinationCode: string;
  DestinationName: string;
  Group: string;
  Line: RailLine | '' | 'No';
  LocationCode: string;
  LocationName: string;
  Min: number | 'ARR' | 'BRD' | '---' | string;
}

export interface RealTimeRailPredictionsResponse {
  Trains: AIMPredictionTrainInfo[];
}

export interface Address {
  Street: string;
  City: string;
  State: string;
  Zip: string;
}

export interface Station {
  Code: string;
  Name: string;
  StationTogether1: string;
  StationTogether2: string;
  LineCode1: string;
  LineCode2: string | null;
  LineCode3: string | null;
  LineCode4: string | null;
  Lat: number;
  Lon: number;
  Address: Address;
}
