import SearchResult from "./search-result";
import { useLocalStorage } from "@uidotdev/usehooks";
import type { SearchResultLocation } from "~/types/search";

export default function SearchHistory() {
    const [searchHistory, setSearchHistory] = useLocalStorage<SearchResultLocation[]>('searchHistory', []);

    const handleClear = () => {
        setSearchHistory([]);
    }
    return (
        <div className="pt-4">
            <div className="flex items-center justify-between px-5 mb-2">
                <h2 className="text-xl font-semibold py-2">Search History</h2>
                {searchHistory.length > 0 && <button className="p-2 text-sm border border-gray-300 rounded-sm bg-white hover:bg-gray-100" onClick={handleClear}>Clear</button>}
            </div>
            { searchHistory.length ? searchHistory.map((location) => {
                return <SearchResult key={location.Key} location={location} />;
            }) : <p className="px-4 text-gray-500">No search history yet.</p>}
        </div>
    )
}