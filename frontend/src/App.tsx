import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

import UnderConstruction from './components/UnderConstruction';
import VehicleList from './features/vehicles/vehiclesList/VehicleList';
import VehicleDetail from './features/vehicles/vehicleForm';
import Dashboard from './features/application/Dashboard';

/**
 * TO DO
 * FRONTEND
 *** Testing
 *** Bubble Up Backend validations inline to form
 *** Migration to RTK / RTK Query
 *** Lazy Loading
 *** API caching
 *** Review loading state(s)
 *** Review error state(s)
 *** Form skeleton
 *** E2E testing
 *
 * BACKEND
 *** Testing
 *** DTOs
 *** Enhanced Vehicle Validation(s)
 *** S3 Image Uploads
 *
 * DONE
 * Pagination
 * Routing
 * Global Error Handling
 * Search
 * Skeleton Loading
 * Mobile First
 * CRUD: Optimistic Deletes
 * CRUD: Create
 * CRUD: Reads (List Vehicles)
 * CRUD: Read (Form population)
 * CRUD: Update (Form Population)
 * CRUD: Optimistic Deletes
 * FE Snapshot Testing
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
