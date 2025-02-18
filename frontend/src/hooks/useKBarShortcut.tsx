import { bindKeyCombo, KeystrokesContext, unbindKeyCombo } from '@rwh/react-keystrokes';
import { useContext, useEffect, useState } from 'react';

// Based on https://github.com/RobertWHurst/Keystrokes/blob/master/packages/react-keystrokes/src/use-key-combo.ts
// but I needed a custom handler and ability to set the state outside the hook.
export function useKBarShortcut(): [boolean, (show: boolean) => void] {
  const [showKBar, setShowKBar] = useState(false);
  const keystrokes = useContext(KeystrokesContext)();

  const updateKBarPressedEffect = () => {
    const showKBar = () => setShowKBar(true);
    const hideKBar = () => setShowKBar(false);
    bindKeyCombo('meta + k', showKBar);
    bindKeyCombo('control + k', showKBar);
    bindKeyCombo('escape', hideKBar);
    return () => {
      unbindKeyCombo('meta + k', showKBar);
      unbindKeyCombo('control + k', showKBar);
      unbindKeyCombo('escape', hideKBar);
    };
  };
  useEffect(updateKBarPressedEffect, [keystrokes]);

  return [showKBar, setShowKBar];
}
