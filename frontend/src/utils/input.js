import { startTransition } from 'react';

// Add test cases around regex patterns
export const containsOnlyNumbers = (value) => /^([0-9]*)$/.test(value);
export const containsOnlyLetters = (value) => /^([A-Za-z]*)$/.test(value);

export const isEnterPressed = (e) => e.key === 'Enter';

export const handleInputFieldSubmit = (value, set, setMeaning, fetch) => {
  set(value);

  startTransition(async () => {
    const result = await fetch(value);
    startTransition(() => {
      setMeaning(result);
    })
  })
};
