import React, { FormEvent, MouseEvent, useState } from "react";
import Button from "../../../../components/Button/Button";
import TextInput from "../../../../components/TextInput/TextInput";

interface SearchPanelParams {
  onSearch: (query: string) => void;
  searching: boolean;
}

const SearchPanel = ({ onSearch, searching }: SearchPanelParams) => {
  const [query, setQuery] = useState("");

  const handleClick = (e: FormEvent | MouseEvent) => {
    e.preventDefault();
    if (query) {
      onSearch(query);
    }
    // TODO handle else as validation
  };

  return (
    <div className="border p-4 max-w-md">
      <form className="grid gap-4 grid-cols-1" onSubmit={handleClick}>
        <label className="block">
          <span className="block text-sm font-medium text-slate-700">
            Type a user name or organization name
          </span>
          <TextInput
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="facebook"
            disabled={searching}
            autoFocus
          />
        </label>

        <Button type="submit" onClick={handleClick} disabled={searching}>
          Search
        </Button>
      </form>
    </div>
  );
};

export default SearchPanel;
