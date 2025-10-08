import React, { FC } from 'react';

import { AnimeItem } from 'src/types';
import { useGetAllAnimesInfiniteQuery } from '../../api/api.slice';
import { Table } from '../../components/Table/Table';

import { MainContainer, Header } from './MainPage.styled';

export const mock = {
  title: 'Title',
  genres: 'Genre',
  type: 'R',
  episodes: 4,
  airedFrom: '20241010',
  airedTo: '20241012',
  rating: 4,
  score: 4,
};

export const getRowsFromData = (data: AnimeItem[]) => data?.map((item) => ({
  title: item.title,
  genres: item.genres,
  type: item.type,
  episodes: item.episodes,
  airedFrom: item.aired.from,
  airedTo: item.aired.to,
  rating: item.rating,
  score: item.score,
  id: item.mal_id,
}
));

export const flattenDataArray = (array: { data: AnimeItem[]; pagination: object }[]): any[] => array?.reduce((acc, item) => {
  if (array && Array.isArray(item.data)) {
    acc.push(...item.data);
  }
  return acc;
}, [] as any[]);

export const MainPage: FC = () => {
  const {
    data, isLoading, fetchNextPage, isFetching,
  } = useGetAllAnimesInfiniteQuery();

  if (isLoading) {
    return <div>Loading</div>;
  }

  const handleNextPage = async () => {
    fetchNextPage();
  };

  return (
    <MainContainer>
      <Header>
        akds
      </Header>
      <Table isFetching={isFetching} fetchNextPage={handleNextPage} rows={getRowsFromData(flattenDataArray(data?.pages))} />
    </MainContainer>
  );
};

export default MainPage;
