import React from 'react';
import ResponsiveAppBar from './Navigation/Appbar';
import CarsList from './Cars/components/CarsList';
import { Routes, Route } from 'react-router-dom';
import CarDetails from './Cars/components/CarDetails';
//import logo from './logo.svg';

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<ResponsiveAppBar />}>
          <Route index element={<CarsList />} />
          <Route path=":id" element={<CarDetails />} />
          {/* <Route path="about" element={<About />} />
          <Route path="dashboard" element={<Dashboard />} />

          {/* Using path="*"" means "match anything", so this route
                acts like a catch-all for URLs that we don't have explicit
                routes for. */}
          {/* <Route path="*" element={<NoMatch />} /> */}
        </Route>
      </Routes>
    </div>
  );
}

export default App;
