import { useState } from 'react';
import { TextField } from '@mui/material';
import CircularProgress from '@mui/material/CircularProgress';
import useMovies from '../hooks/useMovies';
import { useDebouncedValue } from '../hooks/useDebouncedValue';
import ContentCard from './Card';

const Content = () => {
  const [queryText, setQueryText] = useState('Batman');
  const debouncedValue = useDebouncedValue(queryText);
  const { loading, error, data } = useMovies(debouncedValue, 1);
  return (
    <main className="flex gap-y-8 flex-col items-center pt-8 px-4">
      <div className="flex flex-col gap-y-3">
        <h2 className="text-cyan-800 text-center text-xl">
          Search for any movie you like
        </h2>
        <h4 className="text-cyan-600 text-center text-l">
          Movies liked so far: 0
        </h4>
        <TextField
          error={!!error}
          onChange={(e) => setQueryText(e.target.value)}
          label="Search movie"
          defaultValue={queryText}
          value={queryText}
          helperText={error}
        />
      </div>
      {loading && <CircularProgress />}
      <div className="grid grid-cols-3 gap-4 items-center min-h-96 mb-6 max-w-100">
        {!loading &&
          data &&
          data?.map((it) => <ContentCard content={it} key={it.imdbID} />)}
      </div>
    </main>
  );
};

export default Content;
