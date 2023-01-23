import { Navigate, Outlet, useLocation } from 'react-router-dom';
import RoutesConfig from 'routes';
import { useAuth } from 'contexts/authContext';

const AuthenticationRequired = () => {
  const { auth } = useAuth();
  const { pathname } = useLocation();

  return auth ? (
    <Outlet />
  ) : (
    <Navigate
      to={RoutesConfig.login}
      state={{
        from: pathname,
      }}
    />
  );
};

export default AuthenticationRequired;
