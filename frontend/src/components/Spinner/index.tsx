import { BqSpinner } from '@beeq/react';
import React from 'react';

export const Spinner: React.FC = () => (
  <div className="absolute grid place-items-center h-full w-full">
    <BqSpinner size="large" textPosition="bellow">
      <span>Fetching products...</span>
    </BqSpinner>
  </div>
);
