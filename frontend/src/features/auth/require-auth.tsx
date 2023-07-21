import { Navigate, Outlet } from 'react-router';
import useReadStoredValue from '../../shared/hooks/utils/use-read-stored-value';

const RequireAuth = () => {
  const storedToken = useReadStoredValue<string>('token');

  return storedToken ? <Outlet /> : <Navigate to="/" />;
};

export default RequireAuth;
