import { useContext } from 'react';
import { GlobalContext } from '../../shared/context/global';
import { Navigate, Outlet } from 'react-router';

const RequireAuth = () => {
  const { globalState } = useContext(GlobalContext);

  return globalState?.token ? <Outlet /> : <Navigate to="/" />;
};

export default RequireAuth;
