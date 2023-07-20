import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from './views/login/login';
import { GlobalState } from './shared/context/global';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';

const queryClient = new QueryClient();

const App = () => {
  return (
    <div className="bg-slate-100">
      <GlobalState>
        <QueryClientProvider client={queryClient}>
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<Login />} />
            </Routes>
          </BrowserRouter>
        </QueryClientProvider>
      </GlobalState>
    </div>
  );
};

export default App;
