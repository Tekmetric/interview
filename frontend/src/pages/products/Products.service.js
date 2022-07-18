import GetAuthHeader from '../../utils/AuthHeader';
import config from '../../config/config';

const getAll = async (filter = { skip: 0, limit: 3 }) => {
  const requestOptions = {
    method: 'GET',
    headers: GetAuthHeader(),
  };

  const response = await fetch(`${config.apiUrl}/products/category/${filter.category}?limit=${filter.limit}&skip=${filter.skip}&select=id,title,price,rating,stock,thumbnail`, requestOptions);
  const jsonResponse = await response.json();

  if (!response.ok) {
    return { error: jsonResponse };
  }

  localStorage.setItem('user', JSON.stringify(jsonResponse));
  return jsonResponse;
};

const ProductsService = {
  getAll,
};

export default ProductsService;
