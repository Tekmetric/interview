import React, { useMemo } from 'react';

import './Status.css';

const Status = ({
  isExpanded,
  fetchError,
  cacheSize,
  getBoosterStatus,
  onClick,
}) => {
  const logLength = useMemo(
    () => {
      let count = 0;
      if (cacheSize > 0) {
        count++;
      }
      if (getBoosterStatus !== '') {
        count++;
      }
      if (!!fetchError) {
        count++;
      }
      return count;
    },
    [cacheSize, getBoosterStatus, fetchError],
  );

  if (!isExpanded) {
    return (
      <div
        className={`status collapsed ${!!fetchError ? 'error' : ''}`}
        onClick={onClick}
      >
        Log messages: {logLength}
      </div>
    );
  }
  return (
    <div
      className={`status expanded ${!!fetchError ? 'error' : ''}`}
      onClick={onClick}
    >
      <dl>
        <dt>Cache Size</dt>
        <dd>{cacheSize}</dd>
        {getBoosterStatus && (
          <>
            <dt>Status</dt>
            <dd>{getBoosterStatus}</dd>
          </>
        )}
      </dl>
      {fetchError && (
        <div className="errorMessage">Error: {fetchError.message}</div>
      )}
    </div>
  );
};

export { Status };
