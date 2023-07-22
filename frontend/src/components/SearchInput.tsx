import React, { ReactElement } from 'react';
import { TextField } from '@mui/material';

type Props = {
  setSearchText: (searchText: string) => void;
  label: string;
};

const SearchInput = ({ setSearchText, label }: Props): ReactElement => {
  return (
    <div className={'w-full flex flex-row mb-10 justify-center'}>
      <TextField
        className='w-full md:w-2/4 lg:w-1/3 bg-blue-200 shadow-blue-400 shadow-sm'
        onChange={(e) => setSearchText(e.target.value)}
        id='search-input'
        label={label}
        variant={'filled'}
      />
    </div>
  );
};

export default SearchInput;
