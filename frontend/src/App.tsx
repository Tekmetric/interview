import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import UnderConstruction from './components/UnderConstruction';
import VehicleList from './features/vehicles/vehiclesList/VehicleList';
import VehicleDetail from './features/vehicles/vehicleForm';
import Dashboard from './features/application/Dashboard';

/**
 * TO DO
 * FRONTEND
 * Bubble Up Backend validations inline
 * NF: Lazy Loading
 * NF: API caching
 * UI: Tests
 * Review loading state(s)
 * Form skeleton
 *
 * BACKEND
 * DTOs
 * Tests
 * Enhanced Vehicle Validation(s)
 *
 * Done
 * List Items
 * Pagination
 * Routing
 * CRUD: Optimistic Deletes
 * CRUD: Create
 * CRUD: Reads (List)
 * CRUD: Read (Form population)
 * CRUD: Update (Form Population)
 * CRUD: Delete (animation + optimistic)
 * UI: Skeleton Form
 * UI: Mobile Styles
 * UI: Search
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
