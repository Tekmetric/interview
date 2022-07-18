import GetAuthHeader from '../../utils/AuthHeader';
import config from '../../config/config';

const get = async (id) => {
  const requestOptions = {
    method: 'GET',
    headers: GetAuthHeader(),
  };

  const response = await fetch(`${config.apiUrl}/products/${id}`, requestOptions);
  const jsonResponse = await response.json();

  if (!response.ok) {
    return { error: jsonResponse };
  }

  localStorage.setItem('user', JSON.stringify(jsonResponse));
  return jsonResponse;
};

const ProductsService = {
  get,
};

export default ProductsService;
