import React from 'react';
import { View, Text, TextInput, StyleSheet } from 'react-native';

type FilterInputProps = {
  label: string;
  value: string | number | null;
  placehoder?: string;
  onChange: (val: any) => void;
};

export default function FilterInput({ label, value, placehoder, onChange }: FilterInputProps) {
  return (
    <View style={styles.inputContainer}>
      <Text style={styles.label}>{label}</Text>
      <TextInput
        style={styles.textInput}
        value={value?.toString() || ''}
        onChangeText={(val) => onChange(val)}
        placeholder={placehoder}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  inputContainer: {
    marginBottom: 12,
    display: 'flex',
    width: '100%',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  label: { fontWeight: 'bold', marginBottom: 4 },
  textInput: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 6,
    padding: 8,
    width: '60%',
  },
});
