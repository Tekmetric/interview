import { ReactElement } from 'react';
import { ManufacturersRespData } from './api';

export interface ReactComponent {
  children?: ReactElement;
  hideHeader?: boolean;
  hideFooter?: boolean;
}

export interface CardData {
  cardData: ManufacturersRespData['Results'][0];
}
