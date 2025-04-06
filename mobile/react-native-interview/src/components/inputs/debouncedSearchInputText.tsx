import { useEffect, useRef, useState } from 'react';
import { TextInput, StyleSheet, TextInputProps } from 'react-native';
import debounce from 'lodash/debounce';
import type { DebouncedFunc } from 'lodash';

type DebouncedSearchInputProps = {
  onDebouncedChange: (value: string) => void;
  delay?: number;
} & TextInputProps;

export default function DebouncedSearchInput({
  onDebouncedChange,
  delay = 1000,
  style,
  ...props
}: DebouncedSearchInputProps) {
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedRef = useRef<DebouncedFunc<(value: string) => void>>();

  useEffect(() => {
    debouncedRef.current = debounce((value: string) => {
      onDebouncedChange(value);
    }, delay);

    return () => {
      debouncedRef.current?.cancel();
    };
  }, [onDebouncedChange, delay]);

  const handleChange = (text: string) => {
    setSearchTerm(text);
    debouncedRef.current?.(text);
  };

  return (
    <TextInput
      value={searchTerm}
      onChangeText={handleChange}
      style={[styles.input, style]}
      {...props}
    />
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  input: {
    borderWidth: 1,
    padding: 10,
    borderRadius: 8,
    marginBottom: 16,
  },
});
