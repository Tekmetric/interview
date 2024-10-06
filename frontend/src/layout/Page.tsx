import { Outlet } from 'react-router-dom';
import { useQuery } from 'react-query';

import { fetchCategories } from '../api/service';
import { Categories } from '../api/service.types';

import { Menu } from './components';

const retrieveCategories = async (): Promise<Categories[]> => {
  try {
    const response = await fetchCategories();
    // Current API returns 200 status code even if there is no data.
    if (!response.data) throw new Error('No categories found');
    return response.data;
  } catch (error) {
    return Promise.reject(new Error(error instanceof Error ? error.message : String(error)));
  }
};

export const Page: React.FC = () => {
  const { data: categories } = useQuery('categories', retrieveCategories);

  return (
    <>
      <Menu categories={categories} />
      <div className="relative max-w-[1280px] p-m m-i-[auto] md:p-l xl:p-xl">
        <Outlet />
      </div>
    </>
  );
};
