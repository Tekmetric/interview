import React, { FC } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { AnimeItem } from 'src/types';
import { useGetAllAnimesInfiniteQuery } from '../../states/api/api.slice';
import { Table } from '../../components/Table/Table';
import { selectActiveSorting, setActiveSorting, resetActiveSorting } from '../../states/mainPage.slice/mainPage.slice';
import { AppDispatch } from '../../store/store';

import { MainContainer, Header, Button } from './MainPage.styled';

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

export const HEADERS = [
  {
    label: 'Title', key: 'title', isSortable: true, size: (3 / 9) * 100,
  },
  {
    label: 'Genre', key: 'genres', isSortable: false, size: (1 / 9) * 100,
  },
  {
    label: 'Type', key: 'type', isSortable: false, size: (1 / 9) * 100,
  },
  {
    label: 'Episodes', key: 'episodes', isSortable: true, size: (1 / 9) * 100,
  },
  {
    label: 'Aired from', key: 'aired_from', isSortable: false, size: (1 / 9) * 100,
  },
  {
    label: 'Aired to', key: 'aired_to', isSortable: false, size: (1 / 9) * 100,
  },
  {
    label: 'Rating', key: 'rating', isSortable: false, size: (1 / 9) * 100,
  },
  {
    label: 'Score', key: 'score', isSortable: true, size: (1 / 9) * 100,
  },
];

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
  const activeSorting = useSelector(selectActiveSorting);
  const dispatch = useDispatch<AppDispatch>();
  const {
    data, isLoading, fetchNextPage, isFetching,
  } = useGetAllAnimesInfiniteQuery({ sort: activeSorting.sortDirection, orderBy: activeSorting.columnId });

  if (isLoading) {
    return <div>Loading</div>;
  }

  const handleNextPage = async () => {
    fetchNextPage();
  };

  return (
    <MainContainer>
      <Header>
        Infinte Scroll Table With Anime Listings
        <Button type="button" onClick={() => dispatch(resetActiveSorting())}>Reset sorting</Button>
      </Header>
      <Table
        headers={HEADERS}
        isFetching={isFetching}
        fetchNextPage={handleNextPage}
        rows={getRowsFromData(flattenDataArray(data?.pages))}
        activeSorting={activeSorting}
        onSort={(columnId, sortDirection) => dispatch(setActiveSorting({ columnId, sortDirection }))}
      />
    </MainContainer>
  );
};

export default MainPage;
