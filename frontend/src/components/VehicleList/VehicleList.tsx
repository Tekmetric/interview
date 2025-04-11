import styles from './vehicleList.module.css';
import { Vehicle } from '../../features/vehicles/types';

interface VehicleListProps {
  vehicles: Vehicle[];
}

const vehicleList = (vehicles: Vehicle[]) => {
  return vehicles.map((vehicle) => {
    return (
      <tr key={vehicle.id}>
        <td>{vehicle.vin}</td>
        <td>{vehicle.make}</td>
        <td>{vehicle.model}</td>
        <td>{vehicle.modelYear}</td>
      </tr>
    );
  });
};

const VehicleList = (props: VehicleListProps) => {
  const { vehicles } = props;

  if (vehicles.length === 0) return <p>No Vehicles Inventoried</p>;

  return (
    <table className={styles.table}>
      <thead>
        <tr>
          <th>vehicle name</th>
          <th>vehicle make</th>
          <th>vehicle model</th>
          <th>vehicle year</th>
        </tr>
      </thead>
      <tbody>{vehicleList(vehicles)}</tbody>
    </table>
  );
};

export default VehicleList;
