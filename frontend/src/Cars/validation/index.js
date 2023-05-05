import * as yup from 'yup';

const carValidationSchema = yup.object({
  brand: yup.string("Enter the car's brand").required('Brand is required'),
  model: yup.string("Enter the car's model").required('Model is required'),
  url: yup.string("Enter the car's picture").url('Must be a valid URL'),
  color: yup.string("Enter the car's body color"),
  description: yup
    .string('Enter a description')
    .max(1000, 'Description length must be less than 1000 characters'),
  year: yup
    .number("Enter the car's manufacturing year")
    .min(1900, 'Manufacturing Year must be greater than 1900')
    .max(new Date().getFullYear()),
  engineCapacity: yup.number("Enter the car's engine capacity").min(100).max(10000),
  minPrice: yup.number('Enter the minimum price available'),
  maxPrice: yup.number('Enter the maximum price available')
});

export default carValidationSchema;
