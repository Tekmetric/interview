import { Routes, Route } from 'react-router-dom';

import Characters from './components/Characters';
import { AppHeading, NotFoundWrapper } from './components/StyledWidgets';

export default function App() {
  return (
    <>
      <AppHeading>
        <h1>The Rick and Morty</h1>
        <h3>(All Characters)</h3>
      </AppHeading>
      <Routes>
        <Route path='/' element={<Characters />} />
        <Route
          path='*'
          element={
            <NotFoundWrapper>
              <h2>Page not found</h2>
            </NotFoundWrapper>
          }
        />
      </Routes>
    </>
  );
}
