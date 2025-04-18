import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import UnderConstruction from './components/UnderConstruction';
import VehicleList from './features/vehicles/vehiclesList/VehicleList';
import VehicleDetail from './features/vehicles/vehicleForm';
import Dashboard from './features/application/Dashboard';

const App: React.FC = () => {
  return (
    <Router>
      <Dashboard>
        <Routes>
          <Route path="/" element={<VehicleList />} />
          <Route path="/vehicle/:id" element={<VehicleDetail />} />
          <Route path="/vehicle/create" element={<VehicleDetail />} />
          <Route path="/profile" element={<UnderConstruction />} />
          <Route path="/scheduling" element={<UnderConstruction />} />
        </Routes>
      </Dashboard>
    </Router>
  );
};
export default App;
