import 'styled-components';

import type { AppTheme } from './index';

declare module 'styled-components' {
  // Makes `props.theme` fully typed in every styled component and css helper.
  // The empty interface is the documented styled-components augmentation idiom.
  // eslint-disable-next-line @typescript-eslint/no-empty-object-type
  export interface DefaultTheme extends AppTheme {}
}
