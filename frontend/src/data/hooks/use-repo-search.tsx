import { useEffect } from "react";
import { useFetch } from "use-http";

interface Query {
  name?: string;
}
interface Pagination {
  page: number;
  perPage: number;
}

const DEFAULT_PAGINATION = {
  perPage: 10,
  page: 1,
};

export const useRepoSearch = (
  { name }: Query,
  { perPage, page }: Pagination = DEFAULT_PAGINATION
) => {
  const { get, loading, error, data } = useFetch(
    `https://api.github.com/search/repositories`,
    {}
  );

  // Normally we shouldn't have the following hook. But use-http makes the call
  // in the first render, before the query is set. 
  // If use-http supported a parameter like `skip` or `enabled` to control 
  // whether the request should be done or not in the first render, 
  // we wouldn't have to use this workaround.
  // In their GH repo, they have some suggestions about this, but not solved.
  // Eg. @tanstack/react-query has an `enabled` parameter to control this case.
  // Here, I just wanted to try a different and lightweight library. (My first time using use-http)
  // But this library is definetely missing this important feature.
  useEffect(
    () => {
      if (name && !loading) {
        get(`?q=user:${name}+fork:true&per_page=${perPage}&page=${page}`);
      }
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [name, perPage, page]
  );

  return {
    data,
    loading,
    error,
  };
};

export default useRepoSearch;
