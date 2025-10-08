import styled from 'styled-components';

export const AnimeListingContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  flex-direction: column;
`;

export const CardContainer = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
  height: 360px;
  width: 80%;
  background: ${({ theme }) => theme.colors.background};
  padding: ${({ theme }) => theme.spacing.l};
  opacity: ${({ theme }) => theme.opacity.background};
`;

export const Details = styled.div`
  display: flex;
  flex-direction: column;
  align-items: top;
  height: 100%;
  padding-left: ${({ theme }) => theme.spacing.m};
`;
