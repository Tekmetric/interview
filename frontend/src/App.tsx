import { useAuth0 } from '@auth0/auth0-react';
import { Route, Routes } from 'react-router-dom';
import { PageLoader } from './components/PageLoader';
import { AuthenticationGuard } from './components/AuthenticationGuard';
import { HomePage } from './pages/HomePage';
import { ProfilePage } from './pages/ProfilePage';
import { NotFoundPage } from './pages/NotFoundPage';

const App = () => {
  const { isLoading } = useAuth0();

  if (isLoading) {
    return (
      <div className="page-layout">
        <PageLoader />
      </div>
    );
  }

  return (
    <Routes>
      <Route path="/" element={<AuthenticationGuard component={HomePage} />} />
      <Route path="/profile" element={<AuthenticationGuard component={ProfilePage} />} />
      <Route path="*" element={<AuthenticationGuard component={NotFoundPage} />} />
    </Routes>
  );
};

export default App;
