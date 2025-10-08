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
  // genres: item.genres,
  genres: 'g',
  type: item.type,
  episodes: item.episodes,
  airedFrom: item.aired.from,
  airedTo: item.aired.to,
  rating: item.rating,
  score: item.score,
  id: item.mal_id,
}
));

export const MainPage: FC = () => {
  const { data, isLoading, fetchNextPage } = useGetAllAnimesInfiniteQuery();
  // const { data, isLoading } = useGetAllAnimesInfiniteQuery();

  console.log(data?.pages.flat() ?? []);
  console.log('MainPage component rendered');

  if (isLoading) {
    return <div>Loading</div>;
  }

  const handleNextPage = async () => {
    console.log('fetch');
    fetchNextPage();
  };

  return (
    <MainContainer>
      <Header>
        akds
      </Header>
      <Table fetchNextPage={handleNextPage} rows={getRowsFromData((data?.pages.flat() ?? [])[0]?.data)} />
    </MainContainer>
  );
};

export default MainPage;
