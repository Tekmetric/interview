import React, { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';

import { fetchCategories } from '../api/service';
import { Category } from '../api/service.types';

import { Menu } from './components';

export const Page: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);

  useEffect(() => {
    const fetch = async () => {
      try {
        const response = await fetchCategories();
        // Current API returns 200 status code even if there is no data.
        if (!response.data) throw new Error('No categories data found');

        setCategories(response.data);
      } catch (error) {
        // Note: This is a simple error handling, in a real project scenario we should handle it properly.
        console.error(error);
      }
    };

    fetch();
  }, []);

  return (
    <>
      <Menu categories={categories} />
      <div className="relative max-w-[1280px] m-i-[auto] p-m md:p-l xl:p-xl">
        <Outlet />
      </div>
    </>
  );
};
