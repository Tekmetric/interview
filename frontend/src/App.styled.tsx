import styled from 'styled-components';

export const AppContainer = styled.div`
  min-height: 100vh;
  background-size: cover;
  margin: -8px;
  opacity: ${(props) => props.theme?.opacity.background};
`;
