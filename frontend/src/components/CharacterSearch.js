import { useState, useCallback } from 'react';
import { Input, InputAdornment, IconButton } from '@mui/material';
import ClearIcon from '@mui/icons-material/Clear';
import { debounce } from 'lodash';

import { CharacterSearchWrapper } from './StyledWidgets';

export default function CharacterSearch({ characterName, onChangeName }) {
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
    debounced('');
  };

  return (
    <CharacterSearchWrapper>
      <Input
        data-testid='id-input-character-name'
        placeholder={"Your character's name here"}
        value={name}
        sx={{
          maxWidth: '100%',
          width: '400px',
          '.MuiInput-input': {
            textAlign: 'center',
          },
        }}
        onChange={onChange}
        endAdornment={
          name && (
            <InputAdornment position='end'>
              <IconButton data-testid='id-button-clear-name' size='small' onClick={onClear}>
                <ClearIcon />
              </IconButton>
            </InputAdornment>
          )
        }
      />
    </CharacterSearchWrapper>
  );
}
