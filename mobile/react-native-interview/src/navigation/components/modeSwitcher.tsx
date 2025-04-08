import { Feather } from '@expo/vector-icons';
import { useTheme } from '@/context/themeContext';
import HeaderButton from '@/components/buttons/headerButton';

export default function ModeSwitcher() {
  const { mode, theme, toggleMode } = useTheme();

  return (
    <HeaderButton onPress={toggleMode}>
      <Feather name={mode === 'dark' ? 'sun' : 'moon'} size={24} color={theme.colors.text} />
    </HeaderButton>
  );
}
