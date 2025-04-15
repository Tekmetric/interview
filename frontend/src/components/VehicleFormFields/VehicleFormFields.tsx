import { FC } from 'react';
import { Field, ErrorMessage, useFormikContext } from 'formik';
import { getYears } from '../../features/vehicles/utils';

import { VehicleFormFieldsProps } from '../../features/vehicles/types';
import FileUpload from '../VehicleUpload';

import styles from './vehicleFormFields.module.css';

const VehicleFormFields: FC<VehicleFormFieldsProps> = ({
  isDisabled,
  fileRef,
  setPreviewImage,
  setHasClearedImage,
}) => {
  const { setFieldValue } = useFormikContext();

  return (
    <div className={styles.form}>
      <label htmlFor="vin">Vin</label>
      <Field id="vin" name="vin" placeholder="XXXXXXXXXXXXXXXXX" disabled={isDisabled} />
      <ErrorMessage name="vin" component="div" className={styles.error} />

      <label htmlFor="make">Make</label>
      <Field id="make" name="make" placeholder="Nissan" disabled={isDisabled} />
      <ErrorMessage name="make" component="div" className={styles.error} />

      <label htmlFor="model">Model</label>
      <Field id="model" name="model" placeholder="Maxima" disabled={isDisabled} />
      <ErrorMessage name="model" component="div" className={styles.error} />

      <label htmlFor="modelYear">Year</label>
      <Field
        id="modelYear"
        name="modelYear"
        as="select"
        disabled={isDisabled}
        onChange={(e: React.ChangeEvent<HTMLSelectElement>) => {
          setFieldValue('modelYear', parseInt(e.target.value, 10));
        }}
      >
        {getYears()}
      </Field>
      <ErrorMessage name="modelYear" component="div" className={styles.error} />

      <FileUpload
        fileRef={fileRef}
        hasClearedImage={setHasClearedImage}
        isDisabled={isDisabled}
        name="image"
        onPreviewImageChange={setPreviewImage}
      />
    </div>
  );
};

export default VehicleFormFields;
