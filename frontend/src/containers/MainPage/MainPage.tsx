import React, { FC } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { useGetAllAnimesInfiniteQuery } from '../../states/api/api.slice';
import { Table } from '../../components/Table/Table';
import { selectActiveSorting, setActiveSorting, resetActiveSorting } from '../../states/mainPage.slice/mainPage.slice';
import { AppDispatch } from '../../store/store';

import { MainContainer, Header, Button } from './MainPage.styled';
import { getRowsFromData, flattenDataArray } from '../../utils';
import { HEADERS } from '../../constants';

export const MainPage: FC = () => {
  const activeSorting = useSelector(selectActiveSorting);
  const dispatch = useDispatch<AppDispatch>();
  const {
    data, fetchNextPage, isFetching, error,
  } = useGetAllAnimesInfiniteQuery({ sort: activeSorting.sortDirection, orderBy: activeSorting.columnId });

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
        rows={getRowsFromData(flattenDataArray(data?.pages)) || []}
        activeSorting={activeSorting}
        onSort={(columnId, sortDirection) => dispatch(setActiveSorting({ columnId, sortDirection }))}
        hasError={!!error}
      />
    </MainContainer>
  );
};

export default MainPage;
