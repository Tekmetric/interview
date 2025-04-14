import { FC } from 'react';
import { Link } from 'react-router-dom';
import { FaEdit, FaTrash } from 'react-icons/fa';

import { Vehicle } from '../../features/vehicles/types';

import styles from './vehicleTable.module.css';

interface VehicleListProps {
  vehicles: Vehicle[];
  handleDeleteVehicle: (id: number) => void;
  handleSetSelectedVehicle: (vehicle: Vehicle) => void;
}

const renderList = (
  vehicles: Vehicle[],
  handleDeleteVehicle: (id: number) => void,
  handleSetSelectedVehicle: (vehicle: Vehicle) => void
) => {
  return vehicles.map((vehicle) => {
    return (
      <tr key={vehicle.id}>
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
            className="link-button"
            to={`/vehicle/${vehicle.id}`}
            onClick={() => handleSetSelectedVehicle(vehicle)}
          >
            <FaEdit />
          </Link>
        </td>
        <td className={styles.editHeader}>
          <button className="link-button" onClick={() => handleDeleteVehicle(vehicle.id)}>
            <FaTrash />
          </button>
        </td>
      </tr>
    );
  });
};

const VehicleTable: FC<VehicleListProps> = (props: VehicleListProps) => {
  const { vehicles, handleDeleteVehicle, handleSetSelectedVehicle } = props;

  if (vehicles.length === 0) return <p className={styles.empty}>No Vehicles Inventoried</p>;

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
      <tbody>{renderList(vehicles, handleDeleteVehicle, handleSetSelectedVehicle)}</tbody>
    </table>
  );
};

export default VehicleTable;
