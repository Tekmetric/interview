import { useEffect, useRef, useState } from 'react';
import { TextInput, StyleSheet, TextInputProps } from 'react-native';
import debounce from 'lodash/debounce';
import type { DebouncedFunc } from 'lodash';
import { useTheme } from '@/context/themeContext';
import { spacing } from '@/config/theme';

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
  const { theme } = useTheme();
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
      placeholderTextColor={'#666'}
      style={[
        styles.input,
        style,
        { backgroundColor: theme.colors.card, borderColor: theme.colors.border },
      ]}
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
    margin: spacing.md,
    borderWidth: 1,
    borderRadius: spacing.md,
    padding: spacing.sm,
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    zIndex: 1000,
  },
});
