import type { FC } from 'react';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import { FaEdit, FaTrash } from 'react-icons/fa';

import { Vehicle } from '../../features/vehicles/types';

import styles from './vehicleTable.module.css';

const renderList = (
  vehicles: Vehicle[],
  handleDeleteVehicle: (id: number) => void,
  handleSetSelectedVehicle: (vehicle: Vehicle) => void,
  deletingId: number | null
) => {
  return vehicles.map((vehicle) => {
    return (
      <tr key={vehicle.id} className={vehicle.id === deletingId ? styles.deleting : ''}>
        <td>{vehicle.id}</td>
        <td className={styles.mobile}>
          {vehicle.make} {vehicle.model}, {vehicle.modelYear}
        </td>
        <td>{vehicle.vin}</td>
        <td>{vehicle.make}</td>
        <td>{vehicle.model}</td>
        <td>{vehicle.modelYear}</td>
        <td className={styles.editHeader}>
          <Link
            aria-label={`Edit ${vehicle.make} ${vehicle.model}, ${vehicle.modelYear}`}
            className="link-button"
            to={`/vehicle/${vehicle.id}`}
            onClick={() => handleSetSelectedVehicle(vehicle)}
          >
            <FaEdit aria-hidden="true" />
          </Link>
        </td>
        <td className={styles.editHeader}>
          <button
            aria-label={`Delete ${vehicle.make} ${vehicle.model}, ${vehicle.modelYear}`}
            className="link-button"
            onClick={() => handleDeleteVehicle(Number(vehicle.id))}
          >
            <FaTrash aria-hidden="true" />
          </button>
        </td>
      </tr>
    );
  });
};

interface VehicleListProps {
  vehicles: Vehicle[];
  handleDeleteVehicle: (id: number) => void;
  handleSetSelectedVehicle: (vehicle: Vehicle) => void;
}

const VehicleTable: FC<VehicleListProps> = (props: VehicleListProps) => {
  const [deletingId, setDeletingId] = useState<number | null>(null);
  const { vehicles, handleDeleteVehicle, handleSetSelectedVehicle } = props;

  const handleAnimateDelete = (id: number) => {
    setDeletingId(id);
    setTimeout(() => {
      handleDeleteVehicle(id);
      setDeletingId(null);
    }, 300);
  };

  if (vehicles.length === 0) return <p className={styles.empty}>No Results Found</p>;

  return (
    <table className={styles.table}>
      <thead>
        <tr className={styles.table__header}>
          <th>id</th>
          <th className={styles.mobile}>Vehicle</th>
          <th>vin</th>
          <th>make</th>
          <th>model</th>
          <th>year</th>
          <th className={styles.editHeader}>edit</th>
          <th className={styles.editHeader}>delete</th>
        </tr>
      </thead>
      <tbody>
        {renderList(vehicles, handleAnimateDelete, handleSetSelectedVehicle, deletingId)}
      </tbody>
    </table>
  );
};

export default VehicleTable;
