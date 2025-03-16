import { ComponentPropsWithoutRef } from 'react';
import Loader from '../Loader/Loader.tsx';

interface ButtonProps extends ComponentPropsWithoutRef<'button'> {
  isLoading?: boolean;
  type?: 'button' | 'submit' | 'reset';
}

const Button = ({
  className,
  isLoading = false,
  type = 'button',
  ...props
}: ButtonProps) => (
  <button
    type={type}
    {...props}
    className={
      'flex cursor-pointer items-center justify-center rounded-lg border border-gray-300 bg-white px-5 py-2.5 text-base font-medium text-gray-900 hover:bg-gray-50 hover:text-green-500 focus:z-10 focus:ring-4 focus:ring-gray-100 focus:outline-none'
    }
  >
    {isLoading ? (
      <>
        <Loader className="mr-3" />
        {props.children}
      </>
    ) : (
      props.children
    )}
  </button>
);

export default Button;
