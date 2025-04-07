import React, { useEffect, useRef } from 'react';
import { ActivityIndicator, StyleSheet, Text, ViewStyle, Animated } from 'react-native';
import { useTheme } from '@/context/themeContext';
import { typography } from '@/config/theme';
import { spacing } from '@/config/theme';

type Props = {
  message?: string;
  fullScreen?: boolean;
  style?: ViewStyle;
};

export default function LoadingOverlay({ message, fullScreen = true, style }: Props) {
  const { theme } = useTheme();
  const opacity = useRef(new Animated.Value(0)).current;

  useEffect(() => {
    Animated.timing(opacity, {
      toValue: 1,
      duration: 250,
      useNativeDriver: true,
    }).start();
  }, []);

  return (
    <Animated.View style={[fullScreen ? styles.overlay : styles.inline, style, { opacity }]}>
      <ActivityIndicator size="large" color={theme.colors.primary} />
      {message && <Text style={[styles.message, { color: theme.colors.text }]}>{message}</Text>}
    </Animated.View>
  );
}

const styles = StyleSheet.create({
  overlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(0,0,0,0.25)',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 99,
  },
  inline: {
    alignItems: 'center',
    justifyContent: 'center',
    padding: spacing.md,
  },
  message: {
    marginTop: spacing.sm,
    fontSize: typography.fontSize.md,
  },
});
