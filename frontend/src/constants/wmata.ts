import type { Station } from '@customTypes/api/wmata';
import * as stations from '../data/stations.json';

export const ALL_STATIONS = stations.Stations;

export const METRO_COLORS = {
  RD: 'bg-red-500 hover:bg-red-600',
  BL: 'bg-blue-500 hover:bg-blue-600',
  YL: 'bg-yellow-500 hover:bg-yellow-600',
  OR: 'bg-orange-500 hover:bg-orange-600',
  GR: 'bg-green-500 hover:bg-green-600',
  SV: 'bg-gray-400 hover:bg-slate-500'
};

export const METRO_LINE_LABELS = {
  RD: 'Red',
  BL: 'Blue',
  YL: 'Yellow',
  OR: 'Orange',
  GR: 'Green',
  SV: 'Silver'
};

export const STATIONS_BY_CODE = ALL_STATIONS.reduce((acc, station) => {
  acc[station.Code] = station;
  return acc;
}, {} as Record<string, Station>);
