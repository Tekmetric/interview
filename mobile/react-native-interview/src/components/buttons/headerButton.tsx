import { Pressable, StyleSheet, ViewStyle } from 'react-native';
import { ReactNode } from 'react';
import { spacing } from '@/config/theme';

type HeaderButtonProps = {
  onPress: () => void;
  children: ReactNode;
  style?: ViewStyle;
};

export default function HeaderButton({ onPress, children, style }: HeaderButtonProps) {
  return (
    <Pressable onPress={onPress} style={[styles.button, style]} testID="header-button">
      {children}
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    marginHorizontal: spacing.md,
  },
});
