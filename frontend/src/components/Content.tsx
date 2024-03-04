import { TextField, Pagination } from '@mui/material';
import CircularProgress from '@mui/material/CircularProgress';
import useMovies from '../hooks/useMovies';
import { useDebouncedValue } from '../hooks/useDebouncedValue';
import ContentCard from './Card';
import useSearch from '../hooks/useSearch';
import useFavourites from '../hooks/useFavourites';

const Content = () => {
  const { searchQuery, currentPage, setPage, setSearchQuery } = useSearch();
  const debouncedValue = useDebouncedValue(searchQuery);
  const { loading, error, data } = useMovies(debouncedValue, currentPage);
  const { likes } = useFavourites();

  return (
    <main className="flex gap-y-8 flex-col items-center pt-8 px-4">
      <div className="flex flex-col gap-y-3">
        <h2 className="text-cyan-800 text-center text-xl">
          Search for any movie you like
        </h2>
        <h4 className="text-cyan-600 text-center text-l">
          Movies liked so far: {Object.keys(likes).length}
        </h4>
        <TextField
          error={!!error}
          onChange={(e) => setSearchQuery(e.target.value)}
          label="Search movie"
          defaultValue={searchQuery}
          value={searchQuery}
          helperText={error}
        />
      </div>
      {loading && <CircularProgress />}
      <div className="grid grid-cols-3 gap-4 items-center min-h-96 mb-6 max-w-100">
        {!loading &&
          data &&
          data?.map((it) => <ContentCard content={it} key={it.imdbID} />)}
      </div>
      <Pagination
        color="primary"
        size="large"
        count={5}
        page={currentPage}
        className="mb-4"
        onChange={(_, page) => setPage(page)}
        siblingCount={1}
      />
    </main>
  );
};

export default Content;
