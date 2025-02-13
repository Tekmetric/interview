import type { Station } from '@customTypes/api/wmata';
import * as stations from '../data/stations.json';

export const METRO_COLORS = {
  RD: 'bg-red-500',
  BL: 'bg-blue-500',
  YL: 'bg-yellow-500',
  OR: 'bg-orange-500',
  GR: 'bg-green-500',
  SV: 'bg-slate-500'
};

export const STATIONS_BY_LINE = (stations.Stations.reduce((acc, station) => {
  const { LineCode1, LineCode2, LineCode3, LineCode4 } = station;
  if (LineCode1) {
    if (!acc[LineCode1]) {
      acc[LineCode1] = [];
    }
    acc[LineCode1].push(station);
  }
  if (LineCode2) {
    if (!acc[LineCode2]) {
      acc[LineCode2] = [];
    }
    acc[LineCode2].push(station);
  }
  if (LineCode3) {
    if (!acc[LineCode3]) {
      acc[LineCode3] = [];
    }
    acc[LineCode3].push(station);
  }
  if (LineCode4) {
    if (!acc[LineCode4]) {
      acc[LineCode4] = [];
    }
    acc[LineCode4].push(station);
  }
  return acc;
}, {} as Record<string, Station[]>));

export const STATIONS_BY_CODE = stations.Stations.reduce((acc, station) => {
  acc[station.Code] = station;
  return acc;
}, {} as Record<string, Station>);
