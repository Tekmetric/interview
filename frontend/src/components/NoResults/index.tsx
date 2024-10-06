import { BqEmptyState } from '@beeq/react';
import React from 'react';

export const NoResults: React.FC = () => (
  <div className="grid h-full w-full place-items-center m-bs-xl">
    <BqEmptyState className="[&::part(thumbnail)]:m-be-0 [&::part(title)]:text-xl [&::part(title)]:m-be-0">
      <div className="justify-center" slot="thumbnail">
        <img className="size-60" src="/error.svg" alt="No results found" />
      </div>
      No results found
      <div className="text-center text-s text-text-secondary" slot="body">
        Sorry, we couldn't find any results, try again later.
      </div>
    </BqEmptyState>
  </div>
);
