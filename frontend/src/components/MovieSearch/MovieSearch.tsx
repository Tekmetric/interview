import { useState } from "react";
import { useWatchmodeContext } from "../../providers/WatchmodeContextProvider/WatchmodeProvider";
import { WatchmodeResult } from "../../types";

export const MovieSearch = () => {
  const [searchValue, setSearchValue] = useState("");
  const { searchMovies } = useWatchmodeContext();
  const [results, setResults] = useState<WatchmodeResult[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const handleSearch = async () => {
    setIsLoading(true);
    setResults([]);
    await searchMovies(searchValue)
      .then((results) => {
        setResults(results);
        setIsLoading(false);
      })
      .catch(() => {
        setIsLoading(false);
      });
  };

  return (
    <>
      <div className="flex flex-col w-full items-center justify-center mt-2">
        <div className="flex w-1/2 gap-2">
          <input
            className="shadow flex-1 appearance-none border rounded py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            id="search"
            type="text"
            placeholder="Search Movies"
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            autoFocus
          />
          <button
            onClick={handleSearch}
            className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline disabled:opacity-25"
            type="button"
            disabled={!searchValue || isLoading}
          >
            Search
          </button>
        </div>
        {isLoading && (
            <div role="status" className="mt-4">
              <svg
                aria-hidden="true"
                className="inline w-8 h-8 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600"
                viewBox="0 0 100 101"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z"
                  fill="currentColor"
                />
                <path
                  d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z"
                  fill="currentFill"
                />
              </svg>
              <span className="sr-only">Loading...</span>
            </div>
          )}
        <div className="w-1/2 mt-4 gap-3 grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
          {results.map((result: WatchmodeResult) => {
            return (
              <div
                key={result.id}
                className="flex flex-col text-center border-2 border-slate-200 rounded-md overflow-hidden"
              >
                <img
                  className="mb-2 w-full"
                  src={
                    result.image_url ||
                    "https://www.publicdomainpictures.net/pictures/280000/velka/not-found-image-15383864787lu.jpg"
                  }
                />
                <div className="flex flex-col p-2">
                  <div className="text-base font-bold">{result.name}</div>
                  <div className="text-sm">{result.year}</div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </>
  );
};
