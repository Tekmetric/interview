import { BqEmptyState } from '@beeq/react';
import React from 'react';

export const NoResults: React.FC = () => (
  <div className="grid place-items-center m-bs-xl h-full w-full">
    <BqEmptyState className="[&::part(thumbnail)]:m-be-0 [&::part(title)]:text-xl [&::part(title)]:m-be-0">
      <div className="justify-center" slot="thumbnail">
        <img className="size-60" src="/error.svg" alt="No results found" />
      </div>
      No results found
      <span className="text-s text-text-secondary" slot="body">
        Sorry, we couldn't find any results, try again later.
      </span>
    </BqEmptyState>
  </div>
);
