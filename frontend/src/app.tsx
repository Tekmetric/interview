import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from './views/login/login';
import ChapterList from './views/chapter/chapter-list';
import { GlobalState } from './shared/context/global';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import RequireAuth from './features/auth/require-auth';

const queryClient = new QueryClient();

const App = () => {
  return (
    <div className="bg-slate-100">
      <GlobalState>
        <QueryClientProvider client={queryClient}>
          <BrowserRouter>
            <Routes>
              <Route path="/" element={<Login />} />
              <Route element={<RequireAuth />}>
                <Route path="/chapters" element={<ChapterList />} />
              </Route>
            </Routes>
          </BrowserRouter>
        </QueryClientProvider>
      </GlobalState>
    </div>
  );
};

export default App;
