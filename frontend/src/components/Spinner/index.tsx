import { BqSpinner } from '@beeq/react';
import React from 'react';

export const Spinner: React.FC = () => (
  <div className="m-bs-xl grid place-items-center h-full">
    <BqSpinner size="large" textPosition="bellow">
      <span>Fetching products...</span>
    </BqSpinner>
  </div>
);
