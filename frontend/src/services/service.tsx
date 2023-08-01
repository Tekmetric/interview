import axios from 'axios';
import { Product } from '../model/Product';

const BASE_URL = 'https://dummyjson.com';

type ResponseType = {
  data: {
    products: Product[];
  };
};

const getProducts = (): Promise<Product[]> => {
  return axios
    .get(`${BASE_URL}/products`)
    .then((res: ResponseType) => res.data.products);
};

export { getProducts };
