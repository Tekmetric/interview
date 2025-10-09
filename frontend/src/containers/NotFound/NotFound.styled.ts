import styled from 'styled-components';

import { ContainerStyles } from '../../shared/styledComponents';

export const NotFoundContainer = styled.div`
  ${ContainerStyles}
  
  background-color: ${({ theme }) => theme.colors.background};
  opacity: ${({ theme }) => theme.opacity.backgroundLight};
  border-spacing: 0;
`;
