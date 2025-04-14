import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import UnderConstruction from './components/UnderConstruction';
import VehicleList from './features/vehicles/vehiclesList/VehicleList';
import VehicleDetail from './features/vehicles/vehicleForm';
import Dashboard from './features/application/Dashboard';

/**
 * TO DO
 * FRONTEND
 * Vehicle Image Validation(s) + Backend validations
 * UI: Search
 * NF: Lazy Loading
 * NF: API caching
 * UI: Tests
 * Review loading state(s)
 * Form skeleton
 *
 * BACKEND
 * File upload (remove on shutdown)
 * DTOs
 * Tests
 * Vehicle Validation(s)
 *
 * Done
 * List Items
 * Pagination
 * CRUD: Optimistic Deletes
 * CRUD: Add
 * CRUD: Delete
 * CRUD
 * UI: Show individual item (route)
 * UI: Skeleton
 * UI: Mobile Styles
 *
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
