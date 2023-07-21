import { useContext, useEffect, useState } from 'react';
import LoginForm from '../../features/auth/login-form';
import { useNavigate } from 'react-router';
import { GlobalContext } from '../../shared/context/global';
import { useLoginApi } from '../../shared/hooks/api/use-login-api';
import { LoginDataType } from '../../shared/types/login';
import Loader from '../../components/loader';

const Login = () => {
  const apiGrantType = process.env.REACT_APP_API_GRANT_TYPE;
  const apiScope = process.env.REACT_APP_API_SCOPE;
  const [authData, setAuthData] = useState<LoginDataType>({
    client_id: '',
    client_secret: '',
    grant_type: apiGrantType ?? '',
    scope: apiScope ?? '',
  });

  const { data, mutate: login, isLoading, isError } = useLoginApi(authData);

  const { setGlobalState } = useContext(GlobalContext);
  const navigate = useNavigate();

  useEffect(() => {
    if (authData.client_id && authData.client_secret) {
      login();
    }
  }, [authData]);

  useEffect(() => {
    if (data) {
      setGlobalState({ token: data.access_token });
      localStorage.setItem('token', data.access_token);

      if (!isLoading) navigate('/chapters');
    }
  }, [data, isLoading]);

  return (
    <div className="flex flex-col items-center justify-center h-screen">
      {isLoading ? <Loader /> : <LoginForm isError={isError} setAuthData={setAuthData} />}
    </div>
  );
};

export default Login;
