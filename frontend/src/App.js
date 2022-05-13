import { Routes, Route } from "react-router-dom";

import CharactersList from './components/CharactersList';
import { AppHeading, NotFoundWrapper } from './components/StyledWidgets';

export default function App() {
  return (
    <>
      <AppHeading>
        <h1>The Rick and Morty</h1>
        <h3>(All Characters)</h3>
      </AppHeading>
      <Routes>
        <Route path="/" element={<CharactersList />} />
        <Route path="*" element={<NotFoundWrapper><h2>Page not found</h2></NotFoundWrapper>} />
      </Routes>
    </>
  );
}
