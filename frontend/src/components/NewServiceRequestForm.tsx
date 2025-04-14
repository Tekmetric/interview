import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';

type FormValues = {
  customerName: string;
  customerPhone: string;
  vehicleMake: string;
  vehicleModel: string;
  vehicleYear: number | undefined;
  licensePlate: string;
  serviceDescription: string;
  odometerReading: number | undefined;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
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
      status: 'PENDING',
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
        <div>
          <label htmlFor="customerName" className="block text-sm font-medium text-gray-700">
            Customer Name *
          </label>
          <input
            type="text"
            id="customerName"
            {...register('customerName')}
            className={`mt-1 block w-full rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm ${
              errors.customerName ? 'border-red-500' : 'border-gray-300'
            }`}
          />
          {errors.customerName && (
            <p className="mt-1 text-sm text-red-600">{errors.customerName.message}</p>
          )}
        </div>

        <div>
          <label htmlFor="customerPhone" className="block text-sm font-medium text-gray-700">
            Customer Phone *
          </label>
          <input
            type="text"
            id="customerPhone"
            placeholder="1234567890"
            {...register('customerPhone')}
            className={`mt-1 block w-full rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm ${
              errors.customerPhone ? 'border-red-500' : 'border-gray-300'
            }`}
          />
          {errors.customerPhone && (
            <p className="mt-1 text-sm text-red-600">{errors.customerPhone.message}</p>
          )}
        </div>

        <div>
          <label htmlFor="vehicleMake" className="block text-sm font-medium text-gray-700">
            Vehicle Make *
          </label>
          <input
            type="text"
            id="vehicleMake"
            {...register('vehicleMake')}
            className={`mt-1 block w-full rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm ${
              errors.vehicleMake ? 'border-red-500' : 'border-gray-300'
            }`}
          />
          {errors.vehicleMake && (
            <p className="mt-1 text-sm text-red-600">{errors.vehicleMake.message}</p>
          )}
        </div>

        <div>
          <label htmlFor="vehicleModel" className="block text-sm font-medium text-gray-700">
            Vehicle Model *
          </label>
          <input
            type="text"
            id="vehicleModel"
            {...register('vehicleModel')}
            className={`mt-1 block w-full rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm ${
              errors.vehicleModel ? 'border-red-500' : 'border-gray-300'
            }`}
          />
          {errors.vehicleModel && (
            <p className="mt-1 text-sm text-red-600">{errors.vehicleModel.message}</p>
          )}
        </div>

        <div>
          <label htmlFor="vehicleYear" className="block text-sm font-medium text-gray-700">
            Vehicle Year *
          </label>
          <input
            type="number"
            id="vehicleYear"
            {...register('vehicleYear')}
            className={`mt-1 block w-full rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm ${
              errors.vehicleYear ? 'border-red-500' : 'border-gray-300'
            }`}
          />
          {errors.vehicleYear && (
            <p className="mt-1 text-sm text-red-600">{errors.vehicleYear.message}</p>
          )}
        </div>

        <div>
          <label htmlFor="licensePlate" className="block text-sm font-medium text-gray-700">
            License Plate *
          </label>
          <input
            type="text"
            id="licensePlate"
            {...register('licensePlate')}
            className={`mt-1 block w-full rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm ${
              errors.licensePlate ? 'border-red-500' : 'border-gray-300'
            }`}
          />
          {errors.licensePlate && (
            <p className="mt-1 text-sm text-red-600">{errors.licensePlate.message}</p>
          )}
        </div>

        <div>
          <label htmlFor="odometerReading" className="block text-sm font-medium text-gray-700">
            Odometer Reading *
          </label>
          <input
            type="number"
            id="odometerReading"
            {...register('odometerReading')}
            className={`mt-1 block w-full rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm ${
              errors.odometerReading ? 'border-red-500' : 'border-gray-300'
            }`}
          />
          {errors.odometerReading && (
            <p className="mt-1 text-sm text-red-600">{errors.odometerReading.message}</p>
          )}
        </div>

        <div>
          <label htmlFor="status" className="block text-sm font-medium text-gray-700">
            Status *
          </label>
          <select
            id="status"
            {...register('status')}
            className={`mt-1 block w-full rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm ${
              errors.status ? 'border-red-500' : 'border-gray-300'
            }`}
          >
            <option value="PENDING">Pending</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
          {errors.status && <p className="mt-1 text-sm text-red-600">{errors.status.message}</p>}
        </div>
      </div>

      <div>
        <label htmlFor="serviceDescription" className="block text-sm font-medium text-gray-700">
          Service Description
        </label>
        <textarea
          id="serviceDescription"
          rows={3}
          {...register('serviceDescription')}
          className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm"
        />
      </div>

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
