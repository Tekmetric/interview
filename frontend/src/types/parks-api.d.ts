export type EntityType = 'DESTINATION' | 'PARK' | 'ATTRACTION' | 'RESTAURANT' | 'HOTEL' | 'SHOW';
import { ParksApiError } from '@/types/parks-api';
export type LiveStatusType = 'OPERATING' | 'DOWN' | 'CLOSED' | 'REFURBISHMENT';

export type LiveQueue = {
  STANDBY?: { waitTime: number}
}

export type EntityData = {
  id: string;
  name: string;
  entityType: EntityType;
  timezone: string;
  parentId?: string;
  destinationId?: string;
  location?: { latitude: number; longitude: number };
  tags: TagData
}

export type TagData = {
  tag: string;
  tagName: string;
  id: string;
  value: string;
}

export type EntityLiveData = {
  id: string;
  name: string;
  entityType: EntityType;
  status: LiveStatusType;
  queue?: LiveQueue;
  lastUpdated: string;
  operatingHours?: { type: SVGStringList; startTime: string; endTime: string };
}

export type EntityLiveDataResponse = {
  id: string;
  name: string;
  entityType: EntityType;
  timezone: string;
  liveData: Array<EntityLiveData>
}

export type LiveDataParams = {
  parkId: string;
  entityType: EntityType
}
