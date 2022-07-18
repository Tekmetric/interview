import config from '../../config/config';

const logout = () => {
  localStorage.removeItem('user');
};

const login = async (data) => {
  const requestOptions = {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  };

  const response = await fetch(`${config.apiUrl}/auth/login`, requestOptions);
  const jsonResponse = await response.json();

  if (!response.ok) {
    return { error: jsonResponse };
  }

  localStorage.setItem('user', JSON.stringify(jsonResponse));
  return jsonResponse;
};

const loginService = {
  login,
  logout,
};

export default loginService;
