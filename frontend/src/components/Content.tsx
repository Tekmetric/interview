import { useState } from 'react';
import { TextField } from '@mui/material';
import CircularProgress from '@mui/material/CircularProgress';
import useMovies from '../hooks/useMovies';
import { useDebouncedValue } from '../hooks/useDebouncedValue';

const Content = () => {
  const [queryText, setQueryText] = useState('Batman');
  const debouncedValue = useDebouncedValue(queryText);
  const { loading, error, data } = useMovies(debouncedValue, 1);
  return (
    <main className="flex gap-y-8 flex-col items-center mt-8 overflow-scroll">
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
      <div className="flex flex-col items-center flex-1 min-h-96">
        {loading && <CircularProgress />}
        {!loading &&
          data &&
          data?.map((it) => <div key={it.imdbID}>{it.name}</div>)}
      </div>
    </main>
  );
};

export default Content;
