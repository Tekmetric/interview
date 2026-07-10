import { NavLink } from "react-router";
import { useLocalStorage } from "@uidotdev/usehooks";
import type { SearchResultLocation } from "~/types/search";

export default function SearchResult({ location, size = 'large', onSelect = () => {} }: { location: SearchResultLocation, size?: 'small' | 'large', onSelect?: () => void }) {
    const [searchHistory, setSearchHistory] = useLocalStorage<SearchResultLocation[]>('searchHistory', []);

    const addToSearchHistory = (location: SearchResultLocation) => {
        setSearchHistory([location, ...searchHistory.filter(loc => loc.Key !== location.Key)]);
    };

    const handleClick = () => {
        addToSearchHistory(location);
        onSelect();
    }

    return (
        <NavLink to={`/forecast/${location.Key}`} onClick={handleClick}>
            <div className={`relative ${size === 'large' ? 'p-4 pl-12' : 'p-2 pl-8'} bg-white hover:bg-blue-100 cursor-pointer`}>
                <span className={`material-icons-outlined absolute ${size === 'large' ? 'left-4' : 'left-1'} top-1/2 transform -translate-y-1/2 text-gray-500`}>
                    location_on
                </span>
                <div className="flex items-center justify-between">
                    <div>
                        <h2 className={`${size === 'large' ? 'text-lg' : 'text-md'} font-semibold`}>{location.LocalizedName}</h2>
                        <p className={`${size === 'large' ? 'text-sm' : 'text-xs'} text-gray-600`}>{location.AdministrativeArea.LocalizedName}</p>
                    </div>
                    <p className={`${size === 'large' ? 'text-sm' : 'text-xs'} text-gray-600`}>{location.Country.LocalizedName}</p>
                </div>
            </div>
        </NavLink>
    );
}