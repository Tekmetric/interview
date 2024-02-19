import { ReactElement } from 'react';
import { ManufacturersRespData } from './api';

export interface Filters {
    name?: string;
    country?: string;
    tags?: string[]
}

export interface ReactComponent {
  children?: ReactElement;
  props?: any
}

export interface PageComponent extends ReactComponent {
  hideHeader?: boolean;
  hideFooter?: boolean;
}

export interface CardData {
  cardData: ManufacturersRespData['Results'][0];
}

export interface FilterComponent extends ReactComponent {
  filters: Filters;
  setFilters: React.Dispatch<Filters>;
}
