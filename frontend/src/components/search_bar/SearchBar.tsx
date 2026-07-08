import { useState, type FormEvent } from 'react';
import { Button } from '../button/Button';

interface SearchBarProps {
  onSearch: (query: string) => void;
}

export function SearchBar({ onSearch }: SearchBarProps) {
  const [inputValue, setInputValue] = useState('');

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    onSearch(inputValue.trim());
  }

  return (
    <form
      role="search"
      onSubmit={handleSubmit}
      className="flex w-full gap-2"
    >
      <label htmlFor="product-search" className="sr-only">
        Search products
      </label>
      <input
        id="product-search"
        type="search"
        name="q"
        value={inputValue}
        onChange={(event) => setInputValue(event.target.value)}
        placeholder="Search products..."
        className="min-w-0 flex-1 rounded border border-neutral-300 px-3 py-2 text-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600"
      />
      <Button type="submit" variant="primary">
        Search
      </Button>
    </form>
  );
}
