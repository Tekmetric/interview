import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { spacing, typography } from '@/config/theme';
import { useTheme } from '@/context/themeContext';

type ArtCrimeFieldProps = {
  label: string;
  value: string | null | undefined;
};

export default function ArtCrimeField({ label, value }: ArtCrimeFieldProps) {
  const { theme } = useTheme();

  if (!value) return null;

  return (
    <View style={styles.fieldContainer}>
      <Text style={[styles.label, { color: theme.colors.text }]}>{label}</Text>
      <Text style={[styles.value, { color: theme.colors.text }]}>{value}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  fieldContainer: {
    marginBottom: spacing.md,
  },
  label: {
    fontSize: typography.fontSize.md,
    fontWeight: '500',
    marginBottom: spacing.xs,
  },
  value: {
    fontSize: typography.fontSize.md,
    fontWeight: '400',
  },
});
