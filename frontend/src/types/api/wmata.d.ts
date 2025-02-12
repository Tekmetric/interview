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
