import { ReactElement } from 'react';
import { ManufacturersRespData } from './api';

export interface FiltersI {
  name?: string;
  country?: string;
  tags?: string[];
}

export interface ReactComponentI {
  children?: ReactElement;
  props?: any;
}

export interface PageComponentI extends ReactComponentI {
  hideHeader?: boolean;
  hideFooter?: boolean;
}

export interface CardDataI {
  cardData: ManufacturersRespData['Results'][0];
}

export interface FilterComponentI extends ReactComponentI {
  filters: FiltersI;
  setFilters: React.Dispatch<FiltersI>;
}

export interface ModalI extends ReactComponentI {
  onClose: () => void;
  open: boolean;
}
