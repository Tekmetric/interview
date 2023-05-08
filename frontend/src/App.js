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
import { ThemeProvider, createTheme } from '@mui/material';
//import logo from './logo.svg';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: Infinity,
      structuralSharing: false
    }
  }
});

const theme = createTheme({
  typography: {
    h3: {
      fontFamily: 'Montserrat',
      fontWeight: 600
    },
    h5: {
      fontFamily: 'Montserrat',
      fontWeight: 600
    },
    p: {
      fontFamily: 'Open Sans'
    },
    button: {
      fontFamily: 'Montserrat',
      fontWeight: 600
    }
  }
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <QueryClientProvider client={queryClient}>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <div className="App">
            <Routes>
              <Route path="/" element={<ResponsiveAppBar />}>
                <Route index element={<CarsList />} />
                <Route path="/cars" element={<CarsList />} />
                <Route path="/cars/new" element={<CarNew />} />
                <Route path="/cars/:id" element={<CarDetails />} />
                <Route path="/cars/:id/edit" element={<CarEdit />} />
              </Route>
            </Routes>
          </div>
        </LocalizationProvider>
      </QueryClientProvider>
    </ThemeProvider>
  );
}

export default App;
