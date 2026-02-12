import {
  Command,
  CommandEmpty,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";

import { SongCard } from "./SongCard";
import { useDebounce } from "@/hooks/useDebounce";
import { searchTracks } from "@/services/musicApi";

export function Search() {
  const [search, setSearch] = useState("");
  const debouncedSearch = useDebounce(search, 500);

  const {
    data: listOfSongs = [],
    isLoading,
    error,
  } = useQuery({
    queryKey: ["search", debouncedSearch],
    queryFn: () => searchTracks(debouncedSearch),
    enabled: debouncedSearch.length > 0,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });

  const handleSearch = (value: string) => {
    setSearch(value);
  };

  return (
    <Command
      shouldFilter={false}
      className="rounded-lg border border-border shadow-md md:min-w-[450px]"
    >
      <CommandInput
        placeholder="Type to search for music..."
        value={search}
        onValueChange={handleSearch}
      />
      <CommandList>
        {isLoading && debouncedSearch && (
          <CommandEmpty>Searching...</CommandEmpty>
        )}
        {error && <CommandEmpty>Error: Failed to search tracks</CommandEmpty>}
        {!isLoading &&
          !error &&
          listOfSongs.length === 0 &&
          debouncedSearch && <CommandEmpty>No results found.</CommandEmpty>}

        {listOfSongs.map((song, index) => (
          <CommandItem key={`${song.title}-${song.artist}-${index}`}>
            <SongCard
              id={song.id}
              title={song.title}
              artist={song.artist}
              album={song.album}
              image={song.image}
              primaryGenreName={song.primaryGenreName}
            />
          </CommandItem>
        ))}
      </CommandList>
    </Command>
  );
}
