import { spacing, typography } from '@/config/theme';
import { useTheme } from '@/context/themeContext';
import { View, Text, StyleSheet, Pressable } from 'react-native';

type InlineErrorProps = {
  message: string;
  title?: string;
  onRetry?: () => void;
};

export default function InlineError({ message, title, onRetry }: InlineErrorProps) {
  const { theme } = useTheme();
  return (
    <View style={styles.container}>
      {title && <Text style={[styles.title, { color: theme.colors.notification }]}>{title}</Text>}
      <Text style={[styles.text, { color: theme.colors.notification }]}>{message}</Text>
      {onRetry && (
        <Pressable
          onPress={onRetry}
          style={[styles.button, { backgroundColor: theme.colors.primary }]}
        >
          <Text style={styles.buttonText}>Retry</Text>
        </Pressable>
      )}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    padding: spacing.md,
    alignItems: 'center',
    justifyContent: 'center',
    height: '100%',
  },
  text: {
    fontSize: typography.fontSize.md,
    textAlign: 'center',
    marginBottom: spacing.lg,
  },
  title: {
    fontSize: typography.fontSize.lg,
    fontFamily: 'bold',
    marginBottom: spacing.lg,
  },
  button: {
    marginTop: spacing.md,
    padding: spacing.sm,
    width: '80%',
    borderRadius: spacing.md,
    alignItems: 'center',
    justifyContent: 'center',
  },
  buttonText: {
    fontSize: typography.fontSize.md,
    fontFamily: 'bold',
  },
});
