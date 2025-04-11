import React, { useEffect } from 'react';
import { useAppSelector, useAppDispatch } from '../../store/store';
import { fetchVehicles } from './actions';

import VehicleList from '../../components/VehicleList/VehicleList';
import Pagination from '../../components/Pagination/Pagination';

import styles from './vehicleContainer.module.css';

const VehicleContainer: React.FC = () => {
  const dispatch = useAppDispatch();
  const { vehicles, loading, error, page, totalPages } = useAppSelector((state) => state.vehicles);

  useEffect(() => {
    dispatch(fetchVehicles(1));
  }, [dispatch, page]);

  if (loading) return <p>we're working on it...</p>;
  if (error) return <p>an error occurred</p>;

  return (
    <section className={styles.container}>
      <VehicleList vehicles={vehicles} />
      <Pagination />
    </section>
  );
};

export default VehicleContainer;
