import { Fragment, useEffect, useState } from "react";
import { Link, Outlet, useLocation } from "react-router";
import SearchBar from "~/components/search/search-bar";
import SearchHistory from "~/components/search/search-history";
import 'material-icons/iconfont/material-icons.css';
import './index.css';

export default function App() {
    const [searchHistoryVisibility, setSearchHistoryVisibility] = useState(false);

    const toggleSearchHistory = () => {
        setSearchHistoryVisibility(!searchHistoryVisibility);
    };

    const currentLocation = useLocation();

    useEffect(() => {
        setSearchHistoryVisibility(false);
    }, [currentLocation.pathname]);

    return (
        <Fragment>
            <title>Weather Forecast App</title>
            <div className="sticky top-0 bg-white flex h-16 justify-between items-center px-8 border-b border-gray-300 overflow-visible z-20">
                <Link to="/" className="flex items-center space-x-1 font-bold lg:text-xl md:text-lg">
                    <span className="md:inline-block hidden">Weather Forecast App</span>
                    <span className="material-icons-outlined">wb_sunny</span>
                </Link>
                {currentLocation.pathname !== '/' && (
                    <div className="absolute left-1/2 transform -translate-x-1/2 md:w-1/3 w-1/2">
                        <SearchBar size="small" />
                    </div>
                )}
                <div>
                    <button onClick={toggleSearchHistory} className="p-2 border border-gray-300 rounded-md h-[42px] cursor-pointer">
                        {searchHistoryVisibility ?
                            <span className="material-icons-outlined text-gray-500">close</span> :
                            <span className="material-icons-outlined text-gray-500">history</span>
                        }
                    </button>
                </div>
            </div>
            <div className="relative min-h-[calc(100vh-4rem)] bg-gray-100">
                <Outlet />
                <div
                    className={`absolute top-0 w-full h-full z-10 bg-gray-500/75 transition-opacity duration-300 ${searchHistoryVisibility ? 'opacity-100 pointer-events-auto' : 'opacity-0 pointer-events-none'}`}
                    onClick={toggleSearchHistory}
                />
                <div className={`fixed top-16 right-0 bottom-0 lg:w-1/3 md:w-1/2 w-full z-20 bg-white h-100vh overflow-y-scroll transition-transform duration-300 ease-in-out ${searchHistoryVisibility ? 'translate-x-0' : 'translate-x-full'}`}>
                    <SearchHistory />
                </div>
            </div>
        </Fragment>
    );
}