import { useCallback, useEffect, useState } from "react";
import { getLocations } from "~/api";
import SearchResult from "./search-result";
import { useDebounce } from "@uidotdev/usehooks";
import type { SearchResultLocation } from "~/types/search";

export default function SearchBar({ size = 'large' }: { size?: 'small' | 'large' }) {
    const [searchTerm, setSearchTerm] = useState('');
    const [searchResults, setSearchResults] = useState([] as SearchResultLocation[]);
    const [isSearching, setIsSearching] = useState(false);
    const debouncedSearchTerm = useDebounce(searchTerm, 300);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(e.target.value);
    };

    const handleResultClick = useCallback(() => {
        setSearchTerm('');
        setSearchResults([]);
    }, []);

    useEffect(() => {
        const fetchSearchResults = async () => {
            try {
                if (debouncedSearchTerm.trim()) {
                    setIsSearching(true);
                    const results = await getLocations(debouncedSearchTerm);
                    setSearchResults(results);
                } else {
                    setSearchResults([]);
                }
            } catch (error) {
                setSearchResults([]);
            } finally {
                setIsSearching(false);
            }
        };

        if (debouncedSearchTerm.trim()) {
            fetchSearchResults();
        } else {
            setSearchResults([]);
        }
    }, [debouncedSearchTerm]);

    return (
        <div className="relative">
            <div className="relative">
                <span className={`material-icons-outlined absolute ${size === 'large' ? 'left-4' : 'left-2'} top-1/2 transform -translate-y-1/2 text-gray-500`}>
                    search
                </span>
                <input
                    type="text"
                    placeholder="Search for a location..."
                    className={`border border-gray-300 w-full bg-white ${size === 'large' ? 'py-4 pl-12 pr-4 text-lg' : 'py-2 pl-8 pr-2 text-sm'} ${debouncedSearchTerm ? 'rounded-t-md border-b-0' : 'rounded-md'}`}
                    onChange={handleInputChange}
                />
            </div>
            {!isSearching && <div className="absolute top-full left-0 right-0 bg-white">
                {searchResults.length > 0 && searchResults.map((location, index) => {
                    return (
                        <div className={`border border-gray-300 ${index !== 0 ? 'border-t-0' : ''}`} key={location.Key}>
                            <SearchResult location={location} size={size} onSelect={handleResultClick} />
                        </div>
                    );
                })}
                {debouncedSearchTerm && searchResults.length === 0 && <p className="p-4 border border-gray-300">No results found.</p>}
            </div>}
        </div>
    );
}