import { useCallback, useEffect, useState } from 'react';
import { useIntl } from 'react-intl';
import { useSearchParams } from 'react-router';
import styled from 'styled-components';

import { useDebouncedValue } from '../../hooks/useDebouncedValue';
import type { CharacterFilters as Filters } from './api';

export const STATUS_OPTIONS = ['alive', 'dead', 'unknown'] as const;
export const GENDER_OPTIONS = ['female', 'male', 'genderless', 'unknown'] as const;

// The URL is the single source of truth for filters: results are shareable,
// refresh-safe, and the browser back button works on search changes.
export function characterFiltersFromSearchParams(searchParams: URLSearchParams): Filters {
  return {
    name: searchParams.get('name') ?? undefined,
    status: searchParams.get('status') ?? undefined,
    gender: searchParams.get('gender') ?? undefined,
  };
}

const Bar = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: ${({ theme }) => theme.space.sm};
  margin-block: ${({ theme }) => theme.space.md};
`;

const SearchInput = styled.input`
  flex: 1 1 240px;
  padding: ${({ theme }) => `${theme.space.sm} ${theme.space.md}`};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius.pill};
  background: ${({ theme }) => theme.colors.background};
  color: ${({ theme }) => theme.colors.text};

  &::placeholder {
    color: ${({ theme }) => theme.colors.textMuted};
  }
`;

const FilterSelect = styled.select`
  padding: ${({ theme }) => `${theme.space.sm} ${theme.space.md}`};
  border: 1px solid ${({ theme }) => theme.colors.border};
  border-radius: ${({ theme }) => theme.radius.pill};
  background: ${({ theme }) => theme.colors.background};
  color: ${({ theme }) => theme.colors.text};
  cursor: pointer;
`;

function useSetUrlParam() {
  const [, setSearchParams] = useSearchParams();
  return useCallback(
    (key: string, value: string) => {
      setSearchParams(
        (previous) => {
          const next = new URLSearchParams(previous);
          if (value) {
            next.set(key, value);
          } else {
            next.delete(key);
          }
          return next;
        },
        // Filter tweaks replace the history entry — the back button should
        // leave the page, not undo every keystroke.
        { replace: true },
      );
    },
    [setSearchParams],
  );
}

export function CharacterFilters() {
  const intl = useIntl();
  const [searchParams] = useSearchParams();
  const setUrlParam = useSetUrlParam();

  // The input is instant; only the URL (and therefore the API query) waits
  // for the debounce.
  const [nameInput, setNameInput] = useState(searchParams.get('name') ?? '');
  const debouncedName = useDebouncedValue(nameInput, 300);

  useEffect(() => {
    setUrlParam('name', debouncedName);
  }, [debouncedName, setUrlParam]);

  return (
    <Bar>
      <SearchInput
        type="search"
        value={nameInput}
        onChange={(event) => setNameInput(event.target.value)}
        placeholder={intl.formatMessage({ id: 'characters.searchPlaceholder' })}
        aria-label={intl.formatMessage({ id: 'characters.searchLabel' })}
      />
      <FilterSelect
        aria-label={intl.formatMessage({ id: 'characters.filter.status' })}
        value={searchParams.get('status') ?? ''}
        onChange={(event) => setUrlParam('status', event.target.value)}
      >
        <option value="">{intl.formatMessage({ id: 'characters.filter.anyStatus' })}</option>
        {STATUS_OPTIONS.map((status) => (
          <option key={status} value={status}>
            {intl.formatMessage({ id: `character.status.${status}` })}
          </option>
        ))}
      </FilterSelect>
      <FilterSelect
        aria-label={intl.formatMessage({ id: 'characters.filter.gender' })}
        value={searchParams.get('gender') ?? ''}
        onChange={(event) => setUrlParam('gender', event.target.value)}
      >
        <option value="">{intl.formatMessage({ id: 'characters.filter.anyGender' })}</option>
        {GENDER_OPTIONS.map((gender) => (
          <option key={gender} value={gender}>
            {intl.formatMessage({ id: `character.gender.${gender}` })}
          </option>
        ))}
      </FilterSelect>
    </Bar>
  );
}
