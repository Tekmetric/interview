import React, { useEffect, useState, useRef, FC } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { FaArrowLeft } from 'react-icons/fa';
import { Formik, Field, Form, FormikHelpers, ErrorMessage, useField } from 'formik';

import { Vehicle } from '../types';

import { useAppSelector, useAppDispatch } from '../../../store/store';
import { createVehicle, fetchVehicleById, setSelectedVehicle, updateVehicleById } from '../actions';

import styles from './vehicleForm.module.css';

interface Values {
  vin: string;
  model: string;
  modelYear: number;
  make: string;
}

const getYears = () => {
  const currentYear = new Date().getFullYear();
  const years = [];

  for (let year = 1900; year <= currentYear; year++) {
    years.push(year);
  }

  return years.map((year) => {
    return (
      <option key={year} value={year}>
        {year}
      </option>
    );
  });
};

interface FileUploadProps {
  name: string;
  isDisabled: boolean;
  fileRef: React.RefObject<HTMLInputElement | null>;
  onPreviewImageChange: (urlString: string | null) => void;
}

const FileUpload: FC<FileUploadProps> = ({ name, isDisabled, fileRef, onPreviewImageChange }) => {
  const [field, meta, helpers] = useField(name);
  const [fileName, setFileName] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      setFileName(files.length === 1 ? files[0].name : `${files.length} files selected`);

      // Create a preview URL
      const previewUrl = URL.createObjectURL(files[0]);
      onPreviewImageChange?.(previewUrl);
    } else {
      setFileName('');
    }

    helpers.setValue(files);
  };

  // TODO add error messaging
  return (
    <div>
      <label htmlFor="files">Select Image File</label>
      <input
        ref={fileRef}
        type="file"
        onChange={handleChange}
        name={field.name}
        disabled={isDisabled}
      />
      <p>{fileName || 'No Image Uploaded'}</p>
    </div>
  );
};

const VehicleForm: FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const vehicle = useAppSelector((state) => state.vehicles.selectedVehicle);

  const { id } = useParams();
  const vehicleId = Number(id);
  const isEditMode = Boolean(id && vehicle);

  // local state
  const [previewImage, setPreviewImage] = useState<string | null>(null);
  const [isDisabled, setIsDisabled] = useState(true);

  // fetch vehicle if it does not already exist in state
  useEffect(() => {
    if (!vehicle && id) {
      dispatch(fetchVehicleById(Number(id)));
    } else {
      if (!id) setIsDisabled(false);
    }
  }, [dispatch, id, vehicle]);

  // clear selected vehicle of unmount
  useEffect(() => {
    return () => {
      dispatch(setSelectedVehicle(null));
      setIsDisabled(true);
    };
  }, [dispatch]);

  const fileRef = useRef<HTMLInputElement>(null);
  const initialValues: Values = {
    vin: vehicle ? vehicle.vin : '',
    model: vehicle ? vehicle.model : '',
    modelYear: vehicle ? vehicle.modelYear : 2025,
    make: vehicle ? vehicle.make : '',
  };

  const vehicleImage = previewImage ? (
    <img src={previewImage} alt="Selected Vehicle Preview" />
  ) : vehicle?.image ? (
    <img src={vehicle.image} alt={`${vehicle.modelYear} ${vehicle.make} ${vehicle.model}`} />
  ) : (
    <img src={'https://placehold.co/400'} alt="Upload Image" />
  );

  console.log(vehicle);
  return (
    <div className={styles.module}>
      <div className={styles.header}>
        <div className={styles.header__text}>
          <div className={styles.header__buttons}>
            <Link className="link-button" to={`/`}>
              <FaArrowLeft />
              <span>Vehicles</span>
            </Link>
            {isEditMode && (
              <button type="button" onClick={() => setIsDisabled(false)}>
                Edit
              </button>
            )}
          </div>
          <h1>
            {vehicleId && vehicle ? `Edit ${vehicle.make} ${vehicle.model}` : 'Add New Vehicle'}
          </h1>
        </div>
        <div className={styles.header__image}>{vehicleImage}</div>
      </div>

      <Formik
        initialValues={initialValues}
        validate={(values) => {
          const errors: Partial<Values> = {};

          if (!values.vin) {
            errors.vin = 'Vehicle Vin is Required';
          } else if (values.modelYear > 1981 && values.vin.length < 17) {
            errors.vin = 'VIN must be 17 characters';
          }

          if (!values.make) {
            errors.make = 'Vehicle Make is Required';
          }

          if (!values.model) {
            errors.model = 'Vehicle Model is Required';
          }

          return errors;
        }}
        onSubmit={(values: Values, { setSubmitting }: FormikHelpers<Values>) => {
          if (isEditMode) {
            dispatch(updateVehicleById(vehicleId, values as Partial<Vehicle>));
          } else {
            const data = {
              ...values,
              modelYear: Number(values.modelYear),
            };

            dispatch(createVehicle(data));
          }

          setSubmitting(false);
          navigate('/');
        }}
      >
        <Form>
          <div className={styles.form__innerSection}>
            <label htmlFor="vin">Vin</label>
            <Field id="vin" name="vin" placeholder="XXXXXXXXXXXXXXXXX" disabled={isDisabled} />
            <ErrorMessage name="vin" component="div" className={styles.error} />

            <label htmlFor="make">Make</label>
            <Field id="make" name="make" placeholder="Nissan" disabled={isDisabled} />
            <ErrorMessage name="make" component="make" className={styles.error} />

            <label htmlFor="model">Model</label>
            <Field id="model" name="model" placeholder="Maxima" disabled={isDisabled} />
            <ErrorMessage name="model" component="model" className={styles.error} />

            <label htmlFor="modelYear">Year</label>
            <Field
              id="modelYear"
              name="modelYear"
              placeholder="2018"
              as="select"
              disabled={isDisabled}
            >
              {getYears()}
            </Field>
            <ErrorMessage name="modelYear" component="modelYear" className={styles.error} />

            <FileUpload
              isDisabled={isDisabled}
              fileRef={fileRef}
              name="files"
              onPreviewImageChange={setPreviewImage}
            />
          </div>

          <button disabled={isDisabled} type="submit">
            Submit
          </button>
        </Form>
      </Formik>
    </div>
  );
};

export default VehicleForm;
