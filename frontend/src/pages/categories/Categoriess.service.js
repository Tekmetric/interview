import GetAuthHeader from '../../utils/AuthHeader';
import config from '../../config/config';

const getAll = async () => {
  const requestOptions = {
    method: 'GET',
    headers: GetAuthHeader(),
  };

  const response = await fetch(`${config.apiUrl}/products/categories`, requestOptions);
  const jsonResponse = await response.json();

  if (!response.ok) {
    return { error: jsonResponse };
  }

  localStorage.setItem('user', JSON.stringify(jsonResponse));
  return jsonResponse;
};

const CategoriesService = {
  getAll,
};

export default CategoriesService;
