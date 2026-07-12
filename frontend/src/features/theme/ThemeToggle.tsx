import { useIntl } from 'react-intl';
import styled from 'styled-components';

import { useAppDispatch, useAppSelector } from '../../app/hooks';
import { MoonIcon, SunIcon } from '../../components/icons';
import { themeToggled } from './themeSlice';

const ToggleButton = styled.button`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: ${({ theme }) => theme.space.sm};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius.pill};
  background: transparent;
  color: ${({ theme }) => theme.colors.text};
  cursor: pointer;
  transition: background ${({ theme }) => theme.transition.fast};

  &:hover {
    background: ${({ theme }) => theme.colors.surfaceHover};
  }
`;

export function ThemeToggle() {
  const intl = useIntl();
  const dispatch = useAppDispatch();
  const mode = useAppSelector((state) => state.theme.mode);
  const isDark = mode === 'dark';

  // A toggle button keeps a constant name and communicates state via
  // aria-pressed — announced as "Dark theme, toggle button, pressed".
  return (
    <ToggleButton
      type="button"
      aria-pressed={isDark}
      aria-label={intl.formatMessage({ id: 'header.themeToggle' })}
      onClick={() => dispatch(themeToggled())}
    >
      {isDark ? <SunIcon /> : <MoonIcon />}
    </ToggleButton>
  );
}
