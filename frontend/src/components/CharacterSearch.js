import React, { useState, useCallback } from 'react';
import { Input, InputAdornment, IconButton } from '@mui/material';
import { Search as SearchIcon, Clear as ClearIcon } from '@mui/icons-material';
import { debounce } from 'lodash';

import { CharacterSearchWrapper } from './StyledWidgets';

const CharacterSearch = ({ characterName, onChangeName }) => {
  const [name, setName] = useState(characterName);

  const debounced = useCallback(debounce(onChangeName, 500), []);

  const onChange = (e) => {
    let value = e.target.value;
    if (!value.trim()) value = '';
    setName(value);
    debounced(value);
  };

  const onClear = (e) => {
    setName('');
    onChangeName('');
  };

  return (
    <CharacterSearchWrapper>
      <Input
        value={name}
        placeholder={'Search by name'}
        onChange={onChange}
        sx={{
          maxWidth: '100%',
          width: '400px',
        }}
        inputProps={{ 'data-testid': 'id-input-character-name' }}
        startAdornment={
          <InputAdornment position='start'>
            <SearchIcon />
          </InputAdornment>
        }
        endAdornment={
          name && (
            <InputAdornment position='end'>
              <IconButton data-testid='id-button-clear-search' size='small' onClick={onClear}>
                <ClearIcon />
              </IconButton>
            </InputAdornment>
          )
        }
      />
    </CharacterSearchWrapper>
  );
};

export default React.memo(CharacterSearch);
