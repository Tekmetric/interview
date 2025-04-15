import { FC } from 'react';
import { Link } from 'react-router-dom';
import { FaArrowLeft, FaTrash } from 'react-icons/fa';
import styles from './vehicleFormHeader.module.css';
import { VehicleFormHeaderProps } from '../../features/vehicles/types';

const VehicleFormHeader: FC<VehicleFormHeaderProps> = ({
  isEditMode,
  isDisabled,
  onEditClick,
  vehicleTitle,
  imageUrl,
  onDeleteImage,
}) => (
  <div className={styles.header}>
    <div className={styles.text}>
      <div className={styles.buttons}>
        <Link className="link-button" to={`/`}>
          <FaArrowLeft />
          <span>Vehicles</span>
        </Link>
        {isEditMode && (
          <button type="button" className={isDisabled ? styles.active : ''} onClick={onEditClick}>
            Edit
          </button>
        )}
      </div>
      <h1>{vehicleTitle}</h1>
    </div>
    <div className={styles.image}>
      {imageUrl && !isDisabled && (
        <button onClick={onDeleteImage} aria-label="delete image">
          <FaTrash aria-hidden="true" />
        </button>
      )}
      <img src={imageUrl || 'https://placehold.co/400'} alt="Vehicle Preview" />
    </div>
  </div>
);

export default VehicleFormHeader;
