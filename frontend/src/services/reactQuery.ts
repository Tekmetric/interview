import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

// Create wrappers in order to avoid passing queryKey and queryFn each time
export function useRequestProcessor() {
  const queryClient = useQueryClient();

  function query<T>(key, queryFunction, options = {}) {
    return useQuery<T>({
      queryKey: key,
      queryFn: queryFunction,
      ...options
    });
  }

  function mutate(key, mutationFunction, options = {}) {
    return useMutation({
      mutationKey: key,
      mutationFn: mutationFunction,
      onSettled: () => queryClient.invalidateQueries(key),
      ...options
    });
  }

  return { query, mutate };
}
