import { createContext, useContext, useState } from "react";
import { WatchmodeResult } from "../../types";
import { fetchAutocompletSearch } from "../../services/watchmode.service";

export interface WatchmodeProviderInterface {
  searchMovies: (search: string) => Promise<WatchmodeResult[]>;
}

export interface ResultsMap {
  [key: string]: WatchmodeResult[];
}

export const WatchmodeContext = createContext<WatchmodeProviderInterface>({
  searchMovies: () => new Promise(() => {}),
});

export const WatchmodeProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [resultsCache, setResultsCache] = useState<ResultsMap>({});

  const searchMovies = async (search: string) => {
    // check cache for results
    const searchStrLower = search.toString().trim().toLowerCase();
    if (resultsCache[searchStrLower]) {
      return Promise.resolve(resultsCache[searchStrLower]);
    }
    // cache does not exist, call the API
    return fetchAutocompletSearch(searchStrLower).then((data) => {
      setResultsCache((prevState) => {
        return {
          ...prevState,
          [searchStrLower]: data.results,
        };
      });
      return data.results;
    });
  };

  return (
    <WatchmodeContext.Provider
      value={{
        searchMovies,
      }}
    >
      <div className="flex items-start">
        <div className="p-4 text-xs text-nowrap">
          <div className="font-bold underline">Cache Info</div>
          {Object.keys(resultsCache).map((search) => {
            return (
              <div key={search}>
                {search} - {resultsCache[search].length}
              </div>
            );
          })}
          {Object.keys(resultsCache).length === 0 && <div>cache is empty</div>}
        </div>
        {children}
      </div>
    </WatchmodeContext.Provider>
  );
};

export const useWatchmodeContext = () => {
  return useContext(WatchmodeContext);
};
