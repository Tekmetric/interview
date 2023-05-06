import React from 'react';
import ResponsiveAppBar from './Navigation/Appbar';
import CarsList from './Cars/components/CarsList';
import { Routes, Route } from 'react-router-dom';
import CarDetails from './Cars/components/CarDetails';
import CarEdit from './Cars/components/CarEdit';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import CarNew from './Cars/components/CarNew';
import { QueryClient, QueryClientProvider } from 'react-query';
//import logo from './logo.svg';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: Infinity,
      structuralSharing: false
    }
  }
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <LocalizationProvider dateAdapter={AdapterDayjs}>
        <div className="App">
          <Routes>
            <Route path="/" element={<ResponsiveAppBar />}>
              <Route index element={<CarsList />} />
              <Route path="new" element={<CarNew />} />
              <Route path=":id" element={<CarDetails />} />
              <Route path=":id/edit" element={<CarEdit />} />
              {/* <Route path="about" element={<About />} />
            <Route path="dashboard" element={<Dashboard />} />

            {/* Using path="*"" means "match anything", so this route
                  acts like a catch-all for URLs that we don't have explicit
                  routes for. */}
              {/* <Route path="*" element={<NoMatch />} /> */}
            </Route>
          </Routes>
        </div>
      </LocalizationProvider>
    </QueryClientProvider>
  );
}

export default App;
