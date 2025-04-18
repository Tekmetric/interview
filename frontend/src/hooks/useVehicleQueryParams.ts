// hooks/useVehicleQueryParams.ts
import { useSearchParams } from 'react-router-dom';

export const useVehicleQueryParams = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const search = searchParams.get('search') ?? '';
  const page = parseInt(searchParams.get('page') || '1', 10);

  const updateParams = ({ search, page }: Partial<{ search: string; page: string }>) => {
    const currentParams: Record<string, string> = {};
    searchParams.forEach((value, key) => {
      currentParams[key] = value;
    });

    if (search !== undefined) {
      currentParams.search = search;
      currentParams.page = '1';
    }

    if (page !== undefined) {
      currentParams.page = page.toString();
    }

    setSearchParams(currentParams);
  };

  return {
    search,
    page,
    updateParams,
  };
};
