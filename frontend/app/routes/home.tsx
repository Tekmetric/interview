import SearchBar from "~/components/search/search-bar";

export default function Home() {
    return (
        <div className="pt-8 lg:w-1/2 w-3/4 mx-auto">
            <header className="text-center">
                <h2 className="text-2xl font-bold">Weather Forecast App (powered by AccuWeather)</h2>
            </header>
            <main className="mt-8">
                <SearchBar />
            </main>
        </div>
    );
}