import type { FC } from 'react';
import { useState } from 'react';
import { Link } from 'react-router-dom';
import { FaArrowLeft, FaTrash, FaSpinner } from 'react-icons/fa';

import { VehicleFormHeaderProps } from '../../features/vehicles/types';

import CarPlaceHolder from '../../assets/images/car_placeholder.png';
import styles from './vehicleFormHeader.module.css';

const VehicleFormHeader: FC<VehicleFormHeaderProps> = ({
  isEditMode,
  isDisabled,
  onEditClick,
  vehicleTitle,
  imageUrl,
  onDeleteImage,
}) => {
  const [loading, setLoading] = useState(true);

  return (
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
        <div>
          <div className="image-loading-overlay">
            {loading && (
              <div className={styles.loading}>
                <FaSpinner />
              </div>
            )}

            <img
              onLoad={() => setLoading(false)}
              onError={() => setLoading(false)}
              src={imageUrl || CarPlaceHolder}
              alt="Vehicle Preview"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default VehicleFormHeader;
