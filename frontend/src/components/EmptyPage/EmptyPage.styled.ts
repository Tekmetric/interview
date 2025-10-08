import styled from 'styled-components';

export const EmptyPageContainer = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  font-size: ${({ theme }) => theme.fontSize.m};
`;
