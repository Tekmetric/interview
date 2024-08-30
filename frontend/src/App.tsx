import { FunctionComponent, useState } from 'react';
import { Routes, Route } from 'react-router-dom';
import { ThemeProvider, Box, CssBaseline } from '@mui/material';
import { lightTheme, darkTheme } from './utils/theme';
import Home from './views/Home';
import RouterUrl from '@/types/RouterUrl.ts';
import Navbar from '@/components/Navbar';
import TaskView from '@/views/Tasks';

const App: FunctionComponent = () => {
  const [darkMode, setDarkMode] = useState(false);

  return (
    <ThemeProvider theme={darkMode ? darkTheme : lightTheme}>
      <CssBaseline />
      <main>
        <Box display="flex" flexDirection="column" height="100%">
          <Navbar darkMode={darkMode} setDarkMode={setDarkMode} />
          <Routes>
            <Route path={RouterUrl.Home} element={<Home />} />
            <Route path={RouterUrl.Tasks} element={<TaskView />} />
          </Routes>
        </Box>
      </main>
    </ThemeProvider>
  );
};

export default App;
