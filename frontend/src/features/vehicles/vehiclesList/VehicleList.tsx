import type { FC } from 'react';
import ReactPaginate from 'react-paginate';
import { useEffect, useState } from 'react';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';

import { useVehicleQueryParams } from '../../../hooks';
import { useAppSelector, useAppDispatch } from '../../../store/store';
import { deleteVehicleById, fetchVehicles, setSelectedVehicle } from '../actions';
import { Vehicle } from '../../../features/vehicles/types';

import Skeleton from '../../../components/Skeleton';
import ToolBar from '../../../components/Toolbar';
import VehicleTable from '../../../components/VehicleTable/VehicleTable';

import styles from './vehicleList.module.css';

interface VehicleListProps {
  vehicleId?: number;
}

const VehicleList: FC<VehicleListProps> = ({ vehicleId }) => {
  const dispatch = useAppDispatch();
  const [isInitialLoad, setIsInitialLoad] = useState(true);

  const { vehicles, loading } = useAppSelector((state) => state.vehicles);
  const { data, meta } = vehicles;

  const { page, search, updateParams } = useVehicleQueryParams();

  useEffect(() => {
    dispatch(fetchVehicles(page, search));
    setIsInitialLoad(false);
  }, [dispatch, page, search]);

  const handleSetPagination = ({ selected }: { selected: number }) => {
    const selectedPage = selected + 1;
    updateParams({ page: selectedPage.toString() });
  };

  const handleDeleteVehicle = (id: number) => {
    dispatch(deleteVehicleById(id));
  };

  const handleSetSelectedVehicle = (vehicle: Vehicle) => {
    dispatch(setSelectedVehicle(vehicle));
  };

  const handleSearch = (query: string) => {
    updateParams({ search: query });
    dispatch(fetchVehicles(1, query));
  };

  const tableContent = isInitialLoad ? (
    <Skeleton count={1} />
  ) : (
    <VehicleTable
      vehicles={data}
      handleDeleteVehicle={handleDeleteVehicle}
      handleSetSelectedVehicle={handleSetSelectedVehicle}
    />
  );

  const paginationContent = isInitialLoad ? (
    <Skeleton count={1} />
  ) : (
    <ReactPaginate
      previousLabel={<FaChevronLeft />}
      nextLabel={<FaChevronRight />}
      breakLabel={'...'}
      pageCount={meta.totalPages}
      pageRangeDisplayed={5}
      marginPagesDisplayed={2}
      forcePage={meta.totalPages > 0 ? meta.currentPage - 1 : undefined}
      onPageChange={handleSetPagination}
      containerClassName={'pagination'}
      pageClassName={'page-item'}
      pageLinkClassName={'page-link'}
      activeClassName={'active'}
      disabledClassName={'disabled'}
      breakClassName={'break'}
    />
  );

  return (
    <>
      <div className={styles.container__toolbar}>
        <ToolBar
          handleSearch={handleSearch}
          currentSearch={search}
          resultCount={search ? meta.totalItems : undefined}
        />
      </div>
      <div className={styles.container__table}>{tableContent}</div>
      <div className={styles.container__pagination}>{meta.totalPages > 0 && paginationContent}</div>
    </>
  );
};

export default VehicleList;
