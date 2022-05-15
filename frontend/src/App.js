import { Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';

import Characters from './components/Characters';
import { AppHeading, NotFoundWrapper } from './components/StyledWidgets';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#1976d2',
    }
  },
  components: {
    MuiPagination: {
      defaultProps: {
        variant: 'outlined',
        size: 'large',
        sx: {
          '.MuiButtonBase-root': {
            fontSize: '20px',
          },
          '.Mui-selected': {
            fontWeight: 'bold',
          },
        },
      },
    },
  },
});

export default function App() {
  return (
    <ThemeProvider theme={theme}>
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
    </ThemeProvider>
  );
}
