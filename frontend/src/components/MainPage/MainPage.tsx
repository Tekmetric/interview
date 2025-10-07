import React from 'react';

import { useGetAllAnimesQuery } from '../../api/api.slice';

export default function MainPage() {
  const { data, error, isLoading } = useGetAllAnimesQuery();

  console.log({ data, error, isLoading });
  return (
    <div>MainPage</div>
  );
}
