import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import UnderConstruction from './components/UnderConstruction';
import VehicleList from './features/vehicles/vehiclesList/VehicleList';
import VehicleDetail from './features/vehicles/vehicleForm';
import Dashboard from './features/application/Dashboard';

/**
 * TO DO
 * UI: Optimistic Adds
 * UI: Edit
 * UI: Search
 * UI: Filter
 * NF: Preload of Pagination on scroll
 * NF: Lazy Loading
 * NF: API caching
 * UI: Styles
 * UI: Base Line Tests
 *
 * Backend: DTOs
 * Backend: Tests
 *
 * Done
 * List Items
 * Pagination
 * CRUD: Optimistic Deletes
 * UI: Show individual item (route)
 * UI: Skeleton
 * UI: Mobile Styles
 */

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
