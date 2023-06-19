import useFetch from "../common/hooks/useFetch";
import {BASE_URL} from "../constants";

const useGetCategories = () => {
  const { data: categories, loading, error } = useFetch(`${BASE_URL}/products/categories`)

  return { categories, loading, error }
};

export default useGetCategories;