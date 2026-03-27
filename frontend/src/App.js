import React from "react";
import styled, { ThemeProvider } from "styled-components";
import PokemonCardsList from "./components/PokemonCardsList";
import theme from "./theme";

const AppContainer = styled.div`
  min-height: 100vh;
  display: flex;
  flex-direction: column;
`;

const Header = styled.header`
  background-color: ${(props) => props.theme.colors.lightGray};
  padding: ${(props) => props.theme.spacing.xl};
  border-bottom: 1px solid #dee2e6;
  text-align: center;
`;

const Title = styled.h1`
  margin: 0 0 10px 0;
  color: ${(props) => props.theme.colors.primary};
`;

const Subtitle = styled.p`
  margin: 0;
  color: ${(props) => props.theme.colors.secondary};
`;

const Main = styled.main`
  flex: 1;
  min-height: 80vh;
  background-color: ${(props) => props.theme.colors.lightGray};
`;

const App = () => {
  return (
    <ThemeProvider theme={theme}>
      <AppContainer>
        <Header>
          <Title>Pokemon TCG API Explorer</Title>
          <Subtitle>Search and explore Pokemon Trading Cards by set</Subtitle>
        </Header>

        <Main>
          <PokemonCardsList />
        </Main>
      </AppContainer>
    </ThemeProvider>
  );
};

export default App;
