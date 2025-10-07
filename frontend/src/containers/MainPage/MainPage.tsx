import React, { FC } from 'react';

import { AnimeItem } from 'src/types';
import { useGetAllAnimesQuery } from '../../api/api.slice';
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
  ranking: 1200,
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
  ranking: item.rank,
  score: item.score,
}
));

export const MainPage: FC = () => {
  const { data, isLoading } = useGetAllAnimesQuery();

  console.log(data?.data);
  console.log('MainPage component rendered');

  if (isLoading) {
    return <div>Loading</div>;
  }

  return (
    <MainContainer>
      <Header>
        akds
      </Header>
      <Table rows={getRowsFromData(data?.data)} />
    </MainContainer>
  );
};

export default MainPage;
