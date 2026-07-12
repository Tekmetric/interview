import { useIntl } from 'react-intl';
import styled from 'styled-components';

import { useAppDispatch, useAppSelector } from '../../app/hooks';
import { localeChanged, SUPPORTED_LOCALES, type AppLocale } from './localeSlice';

// Each locale is shown in its own language so users can always find theirs.
const LOCALE_LABELS: Record<AppLocale, string> = {
  'en-US': 'English',
  'ro-RO': 'Română',
};

const Select = styled.select`
  padding: ${({ theme }) => `${theme.space.xs} ${theme.space.sm}`};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius.sm};
  background: ${({ theme }) => theme.colors.background};
  color: ${({ theme }) => theme.colors.text};
  cursor: pointer;
`;

export function LocaleSwitcher() {
  const intl = useIntl();
  const dispatch = useAppDispatch();
  const locale = useAppSelector((state) => state.locale.locale);

  return (
    <Select
      aria-label={intl.formatMessage({ id: 'header.localeLabel' })}
      value={locale}
      onChange={(event) => dispatch(localeChanged(event.target.value as AppLocale))}
    >
      {SUPPORTED_LOCALES.map((supported) => (
        <option key={supported} value={supported}>
          {LOCALE_LABELS[supported]}
        </option>
      ))}
    </Select>
  );
}
