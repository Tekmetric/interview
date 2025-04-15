import { Vehicle } from './types';

export const convertVehicleToFormData = (vehicle: Partial<Vehicle>) => {
  const formData = new FormData();

  formData.append('model', vehicle.model ?? '');
  formData.append('make', vehicle.make ?? '');
  formData.append('modelYear', vehicle.modelYear?.toString() ?? '');
  formData.append('vin', vehicle.vin ?? '');

  if (vehicle.image instanceof File) {
    formData.append('image', vehicle.image);
  } else if (vehicle.image === null) {
    formData.append('removeImage', 'true');
  }

  return formData;
};

export const getYears = () => {
  const currentYear = new Date().getFullYear();
  const years = [];

  for (let year = 1950; year <= currentYear; year++) {
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
