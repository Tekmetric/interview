import styled from 'styled-components';
import { ContainerStyles } from '../../shared/styledComponents';

export const AnimeListingContainer = styled.div`
  ${ContainerStyles}
`;

export const CardContainer = styled.div<{ $centered?: boolean }>`
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: ${({ $centered }) => $centered && 'center'};

  height: 360px;
  width: 80%;

  padding: ${({ theme }) => theme.spacing.l};

  background: ${({ theme }) => theme.colors.background};
  opacity: ${({ theme }) => theme.opacity.background};
`;

export const Details = styled.div`
  display: flex;
  flex-direction: column;
  align-items: top;

  height: 100%;

  padding-left: ${({ theme }) => theme.spacing.m};
`;
