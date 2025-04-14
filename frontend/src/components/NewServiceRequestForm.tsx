import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { Input, TextArea, Select } from './ui';
import { RepairServiceStatus, getAllStatuses } from '../types/enums';

type FormValues = {
  customerName: string;
  customerPhone: string;
  vehicleMake: string;
  vehicleModel: string;
  vehicleYear: number | undefined;
  licensePlate: string;
  serviceDescription: string;
  odometerReading: number | undefined;
  status: RepairServiceStatus;
};

type NewServiceRequestFormProps = {
  onSave: (data: FormValues) => void;
  onCancel: () => void;
  initialValues?: FormValues;
};

const validationSchema = yup.object().shape({
  customerName: yup
    .string()
    .required('Customer name is required')
    .min(2, 'Customer name must be at least 2 characters')
    .max(100, 'Customer name must be less than 100 characters'),
  customerPhone: yup
    .string()
    .required('Customer phone is required')
    .matches(/^\d{10}$/, 'Phone number must be 10 digits'),
  vehicleMake: yup.string().required('Vehicle make is required'),
  vehicleModel: yup.string().required('Vehicle model is required'),
  vehicleYear: yup
    .number()
    .required('Vehicle year is required')
    .positive('Vehicle year must be a positive number')
    .typeError('Vehicle year must be a number'),
  licensePlate: yup.string().required('License plate is required'),
  serviceDescription: yup.string().nullable(),
  odometerReading: yup
    .number()
    .required('Odometer reading is required')
    .positive('Odometer reading must be a positive number')
    .typeError('Odometer reading must be a number'),
  status: yup.string().required('Status is required'),
});

export const NewServiceRequestForm = ({
  onSave,
  onCancel,
  initialValues,
}: NewServiceRequestFormProps) => {
  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
    reset,
  } = useForm<FormValues>({
    resolver: yupResolver(validationSchema) as any,
    mode: 'onChange',
    defaultValues: initialValues || {
      customerName: '',
      customerPhone: '',
      vehicleMake: '',
      vehicleModel: '',
      vehicleYear: undefined,
      licensePlate: '',
      serviceDescription: '',
      odometerReading: undefined,
      status: RepairServiceStatus.PENDING,
    },
  });

  const onSubmit = (data: any) => {
    const processedData = {
      ...data,
      vehicleYear: data.vehicleYear ? Number(data.vehicleYear) : undefined,
      odometerReading: data.odometerReading ? Number(data.odometerReading) : undefined,
    };
    onSave(processedData as FormValues);
    reset();
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Input
          id="customerName"
          label="Customer Name"
          type="text"
          required
          error={errors.customerName}
          {...register('customerName')}
        />

        <Input
          id="customerPhone"
          label="Customer Phone"
          type="text"
          required
          placeholder="1234567890"
          error={errors.customerPhone}
          {...register('customerPhone')}
        />

        <Input
          id="vehicleMake"
          label="Vehicle Make"
          type="text"
          required
          error={errors.vehicleMake}
          {...register('vehicleMake')}
        />

        <Input
          id="vehicleModel"
          label="Vehicle Model"
          type="text"
          required
          error={errors.vehicleModel}
          {...register('vehicleModel')}
        />

        <Input
          id="vehicleYear"
          label="Vehicle Year"
          type="number"
          required
          error={errors.vehicleYear}
          {...register('vehicleYear')}
        />

        <Input
          id="licensePlate"
          label="License Plate"
          type="text"
          required
          error={errors.licensePlate}
          {...register('licensePlate')}
        />

        <Input
          id="odometerReading"
          label="Odometer Reading"
          type="number"
          required
          error={errors.odometerReading}
          {...register('odometerReading')}
        />

        <Select id="status" label="Status" required error={errors.status} {...register('status')}>
          {getAllStatuses().map(status => (
            <option key={status} value={status}>
              {status.replace('_', ' ')}
            </option>
          ))}
        </Select>
      </div>

      <TextArea
        id="serviceDescription"
        label="Service Description"
        rows={3}
        {...register('serviceDescription')}
      />

      <div className="flex justify-end space-x-3">
        <button
          type="button"
          onClick={onCancel}
          className="px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          Discard
        </button>
        <button
          type="submit"
          disabled={!isValid}
          className={`px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white ${
            !isValid
              ? 'bg-blue-300 cursor-not-allowed'
              : 'bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500'
          }`}
        >
          Save
        </button>
      </div>
    </form>
  );
};
