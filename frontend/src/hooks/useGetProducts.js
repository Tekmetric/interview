import useFetch from "../common/hooks/useFetch";
import {BASE_URL} from "../constants";

const useGetProducts = (category, selectedSort, limit) => {
  const url = `${BASE_URL}/products${category ? `/category/${category}` : ''}?sort=${selectedSort}&limit=${limit}`;

  const { data: products, loading, error } = useFetch(url)

  return { products, loading, error }
};

export default useGetProducts;