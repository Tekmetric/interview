import { TouchableOpacity } from 'react-native';
import { Feather } from '@expo/vector-icons';
import { useTheme } from '../../context/themeContext';

export default function ModeSwitcher() {
  const { mode, toggleMode } = useTheme();
  return (
    <TouchableOpacity onPress={toggleMode}>
      {mode === 'dark' ? (
        <Feather name="sun" size={24} color="#FFD700" /> // Light mode icon
      ) : (
        <Feather name="moon" size={24} color="#333" /> // Dark mode icon
      )}
    </TouchableOpacity>
  );
}
