import { useFetch } from '../helpers/useFetch';
import type { Dog } from './types';

const Column = () => {
  const {
    data: dogs,
    loading,
    error,
  }: { data: Array<Dog>, loading: boolean, error: string | null} = useFetch(
    'https://api.thedogapi.com/v1/breeds?limit=5&page=1'
  );
    console.log("in column")
  return (
    <>
      {loading && <div>Loading...</div>}
      {error && <div>Error: {error.toString()}</div>}
      {dogs && !error && !loading && (
        <div className="flex flex-col mx-10">
          <h2 className="text-2xl font-bold text-gray-800">
            Dogs
          </h2>
          <div data-testid="dog-list">
            {dogs.map((dog: any) => {
              return (
                <div
                  key={dog.id}
                  className="my-10 flex max-w-sm flex-col justify-center rounded-xl border bg-white shadow-sm dark:border-gray-700 dark:bg-gray-800 dark:shadow-slate-700/[.7]"
                >
                  <img
                    className="mx-auto mt-3 h-auto w-[14rem] rounded-xl"
                    src={dog.image?.url}
                    alt={dog.name}
                  />
                  <div className="p-4 md:p-5">
                    <h3 className="text-lg font-bold text-gray-800 dark:text-white">
                      {dog.name}
                    </h3>
                    <p className="mt-1 text-gray-800 dark:text-gray-400">
                      <strong>Life Span:</strong> {dog.life_span}
                      <br />
                      <strong>Temperament:</strong> {dog.temperament}
                    </p>
                  </div>
                  <button
                    type="button"
                    className="inline-flex items-center justify-center gap-2 rounded-md border-2 border-gray-200 px-4 py-[.688rem] text-sm font-semibold text-blue-500 transition-all hover:border-blue-500 hover:bg-blue-500 hover:text-white focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 dark:border-gray-700 dark:hover:border-blue-500"
                  >
                    Favourite
                  </button>
                </div>
              );
            })}
          </div>
        </div>
      )}
    </>
  );
};

export default Column;
