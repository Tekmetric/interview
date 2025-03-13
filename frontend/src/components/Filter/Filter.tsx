import { useEffect, useState } from 'react';

import useDebouncedValue from '../../hooks/useDebouncedValue.tsx';
import { useFilter } from '../../providers/FilterProvider.tsx';
import TextInput from '../TextInput/TextInput.tsx';

import { SearchIcon } from 'lucide-react';

const Filter = () => {
  const { setSearchTerm, searchTerm } = useFilter();
  const [search, setSearch] = useState(searchTerm);
  const debouncedValue = useDebouncedValue<string>(search);

  useEffect(() => {
    setSearchTerm(debouncedValue);
  }, [debouncedValue, setSearchTerm]);

  return (
    <TextInput
      className="mx-auto w-full"
      icon={<SearchIcon className="mx-4 my-3" />}
      name="search"
      label="Search products"
      value={search}
      onChange={(e) => setSearch(e.target.value)}
    />
  );
};

export default Filter;
