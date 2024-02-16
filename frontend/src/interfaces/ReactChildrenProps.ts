import { ReactElement } from 'react';

export default interface ReactComponent {
  children?: ReactElement;
  props?: {
    hideHeader?: boolean;
    hideFooter?: boolean;
  };
}
