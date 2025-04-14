import { FC, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import ReactPaginate from 'react-paginate';
import { FaChevronLeft, FaChevronRight } from 'react-icons/fa';

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
  const [searchParams, setSearchParams] = useSearchParams();
  const currentPageFromUrl = parseInt(searchParams.get('page') || '1', 10);

  const { vehicles, loading, error } = useAppSelector((state) => state.vehicles);
  const { data, meta } = vehicles;

  useEffect(() => {
    dispatch(fetchVehicles(currentPageFromUrl));
  }, [dispatch, currentPageFromUrl]);

  const handleSetPagination = (newPage: any) => {
    const selectedPage = newPage.selected + 1;
    setSearchParams({ page: selectedPage.toString() });

    dispatch(fetchVehicles(selectedPage));
  };

  const handleDeleteVehicle = (id: number) => {
    dispatch(deleteVehicleById(id));
  };

  const handleSetSelectedVehicle = (vehicle: Vehicle) => {
    dispatch(setSelectedVehicle(vehicle));
  };

  const tableContent = loading ? (
    <Skeleton count={1} />
  ) : (
    <VehicleTable
      vehicles={data}
      handleDeleteVehicle={handleDeleteVehicle}
      handleSetSelectedVehicle={handleSetSelectedVehicle}
    />
  );

  const paginationContent = loading ? (
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
        <ToolBar />
      </div>
      <div className={styles.container__table}>{tableContent}</div>
      <div className={styles.container__pagination}>{paginationContent}</div>
    </>
  );
};

export default VehicleList;
