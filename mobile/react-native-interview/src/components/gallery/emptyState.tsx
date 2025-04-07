import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { Feather } from '@expo/vector-icons';
import { spacing, typography } from '@/config/theme';
import { useTheme } from '@/context/themeContext';

type EmptyStateProps = {
  icon?: keyof typeof Feather.glyphMap;
  title: string;
  description: string;
};

export default function EmptyState({ icon = 'inbox', title, description }: EmptyStateProps) {
  const { theme } = useTheme();

  return (
    <View style={styles.container}>
      <View style={[styles.iconContainer, { backgroundColor: theme.colors.card }]}>
        <Feather name={icon} size={32} color={theme.colors.primary} />
      </View>
      <Text style={[styles.title, { color: theme.colors.text }]}>{title}</Text>
      <Text style={[styles.description, { color: theme.colors.text }]}>{description}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: spacing.xl,
  },
  iconContainer: {
    width: 80,
    height: 80,
    borderRadius: 40,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: spacing.lg,
  },
  title: {
    fontSize: typography.fontSize.lg,
    fontWeight: 'bold',
    textAlign: 'center',
    marginBottom: spacing.sm,
  },
  description: {
    fontSize: typography.fontSize.md,
    textAlign: 'center',
    opacity: 0.7,
  },
});
