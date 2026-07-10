import { useId, useState, type FormEvent } from 'react';
import { formControlClassName } from '../../styles/formControl';
import { Button } from '../button/Button';

interface SearchBarProps {
  onSearch: (query: string) => void;
}

export function SearchBar({ onSearch }: SearchBarProps) {
  const inputId = useId();
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
      <label htmlFor={inputId} className="sr-only">
        Search products
      </label>
      <input
        id={inputId}
        type="search"
        name="q"
        value={inputValue}
        onChange={(event) => setInputValue(event.target.value)}
        placeholder="Search products..."
        className={`${formControlClassName} min-w-0 flex-1 px-3 py-2 text-text`}
      />
      <Button type="submit" variant="primary">
        Search
      </Button>
    </form>
  );
}
