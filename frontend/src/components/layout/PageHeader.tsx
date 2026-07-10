import { ViewCartButton } from '../add_to_cart/ViewCartButton';
import { SearchBar } from '../search_bar/SearchBar';

interface PageHeaderProps {
  onSearch: (query: string) => void;
}

export function PageHeader({ onSearch }: PageHeaderProps) {
  return (
    <header className="border-b border-border bg-elevated">
      <div className="max-w-7xl mx-auto px-4 py-4">
        <div className="hidden md:grid md:grid-cols-3 md:items-center md:gap-4">
          <div className="justify-self-start">
            <span className="text-lg font-bold text-text">Productpalooza</span>
          </div>
          <div className="w-full max-w-md justify-self-center">
            <SearchBar onSearch={onSearch} />
          </div>
          <div className="justify-self-end">
            <ViewCartButton />
          </div>
        </div>

        <div className="md:hidden space-y-4">
          <span className="text-lg font-bold text-text">Productpalooza</span>
          <div className="grid grid-cols-[1fr_auto] items-center gap-4">
            <SearchBar onSearch={onSearch} />
            <ViewCartButton />
          </div>
        </div>
      </div>
    </header>
  );
}
