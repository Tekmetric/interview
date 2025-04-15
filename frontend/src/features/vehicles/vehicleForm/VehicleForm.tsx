import type { FC } from 'react';
import { useEffect, useState, useRef } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Formik, Form, FormikHelpers } from 'formik';

import { useAppSelector, useAppDispatch } from '../../../store/store';
import { createVehicle, fetchVehicleById, setSelectedVehicle, updateVehicleById } from '../actions';

import VehicleFormHeader from '../../../components/VehicleFormHeader';
import VehicleFormFields from '../../../components/VehicleFormFields';

import styles from './vehicleForm.module.css';

interface Values {
  vin: string;
  model: string;
  modelYear: number;
  make: string;
  image: FileList | undefined | string;
}

const VehicleForm: FC = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const vehicle = useAppSelector((state) => state.vehicles.selectedVehicle);

  const { id } = useParams();
  const vehicleId = Number(id);
  const isEditMode = Boolean(id && vehicle);

  const [previewImage, setPreviewImage] = useState<string | null>(null);
  const [isDisabled, setIsDisabled] = useState(true);
  const [hasClearedImage, setHasClearedImage] = useState(false);

  const fileRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (!vehicle && id) {
      dispatch(fetchVehicleById(vehicleId));
    } else {
      if (!id) setIsDisabled(false);
    }
  }, [dispatch, id, vehicle]);

  useEffect(() => {
    return () => {
      dispatch(setSelectedVehicle(null));
      setIsDisabled(true);
    };
  }, [dispatch]);

  const initialValues: Values = {
    vin: vehicle?.vin ?? '',
    model: vehicle?.model ?? '',
    modelYear: vehicle?.modelYear ?? 2025,
    make: vehicle?.make ?? '',
    image: vehicle?.image ?? '',
  };

  const deleteLocalImage = () => {
    if (fileRef.current) {
      fileRef.current.value = '';
    }

    setPreviewImage(null);
    setHasClearedImage(true);
  };

  return (
    <Formik
      enableReinitialize
      initialValues={initialValues}
      validate={(values) => {
        const errors: Partial<Values> = {};

        if (!values.vin) {
          errors.vin = 'Vehicle VIN is required';
        } else if (values.modelYear > 1981 && values.vin.length < 17) {
          errors.vin = 'VIN must be 17 characters';
        }

        if (!values.make) {
          errors.make = 'Vehicle Make is required';
        }

        if (!values.model) {
          errors.model = 'Vehicle Model is required';
        }

        return errors;
      }}
      onSubmit={async (values: Values, { setSubmitting }: FormikHelpers<Values>) => {
        const imageValue =
          typeof values.image === 'string' ? undefined : hasClearedImage ? null : values.image?.[0];

        const data = {
          ...values,
          image: imageValue,
        };

        let success = false;
        if (isEditMode) {
          success = await dispatch(updateVehicleById(vehicleId, data));
        } else {
          success = await dispatch(createVehicle(data));
        }

        if (success) {
          navigate('/');
        }

        setSubmitting(false);
      }}
    >
      {() => (
        <div className={styles.module}>
          <VehicleFormHeader
            isEditMode={isEditMode}
            isDisabled={isDisabled}
            onEditClick={() => setIsDisabled(false)}
            vehicleTitle={
              vehicleId && vehicle ? `Edit ${vehicle.make} ${vehicle.model}` : 'Add New Vehicle'
            }
            imageUrl={previewImage || (vehicle?.image && !hasClearedImage ? vehicle.image : null)}
            onDeleteImage={deleteLocalImage}
          />

          <Form>
            <VehicleFormFields
              isDisabled={isDisabled}
              fileRef={fileRef}
              setPreviewImage={setPreviewImage}
              setHasClearedImage={setHasClearedImage}
            />

            <button disabled={isDisabled} type="submit">
              Submit
            </button>
          </Form>
        </div>
      )}
    </Formik>
  );
};

export default VehicleForm;
