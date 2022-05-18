import { Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';

import Characters from './components/Characters';
import {
  StickyContent,
  AppHeading,
  NotFoundWrapper,
  AppFooter,
  CopyrightInfo,
} from './components/StyledWidgets';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#1976d2',
    },
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
          '.MuiPaginationItem-previousNext': {
            border: '1px solid rgba(100, 255, 100, 0.24)',
            backgroundColor: 'rgba(100, 255, 100, 0.24)',
          },
          '.MuiPaginationItem-firstLast': {
            border: '1px solid rgba(255, 70, 70, 0.24)',
            backgroundColor: 'rgba(255, 70, 70, 0.24)',
          },
        },
      },
    },
  },
});

export default function App() {
  return (
    <ThemeProvider theme={theme}>
      <StickyContent>
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
      </StickyContent>
      <AppFooter>
        <CopyrightInfo>
          © 2022&nbsp;
          <a href='https://www.linkedin.com/in/nickiesong' target='_blank' rel='noreferrer'>
            Nicholas Song
          </a>
          . All rights reserved.
        </CopyrightInfo>
      </AppFooter>
    </ThemeProvider>
  );
}
