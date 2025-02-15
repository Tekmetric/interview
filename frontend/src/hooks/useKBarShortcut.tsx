import { bindKeyCombo } from '@rwh/react-keystrokes';
import { useState } from 'react';

export function useKBarShortcut(): [boolean, (show: boolean) => void] {
  const [showKBar, setShowKBar] = useState(false);
  bindKeyCombo('meta + k', () => setShowKBar(true));
  bindKeyCombo('control + k', () => setShowKBar(true));
  bindKeyCombo('escape', () => setShowKBar(false));

  return [showKBar, setShowKBar];
}
