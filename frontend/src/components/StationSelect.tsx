import type { StationSelectOption } from '@customTypes/components/StationSelect';
import type { ReactNode } from 'react';
import type { ActionMeta, SelectInstance } from 'react-select';
import { STATIONS_BY_CODE } from '@constants/wmata';
import createCache from '@emotion/cache';
import { CacheProvider } from '@emotion/react';
import { useNavigate } from '@tanstack/react-router';
import { useLocalStorage } from '@uidotdev/usehooks';
import { useMemo, useRef } from 'react';
import Select from 'react-select';

/**
 * Ensures the Emotion CSS classes are inserted _before_ the custom CSS from the root index.css.
 */
const EmotionCacheProvider = ({ children }: { children: ReactNode }) => {
  const cache = useMemo(
    () =>
      createCache({
        key: 'with-tailwind',
        insertionPoint: document.querySelector('title')!
      }),
    []
  );

  return <CacheProvider value={ cache }>{children}</CacheProvider>;
};

export function StationSelect({
  autoFocus = true,
  isClearable = true,
  isDisabled = false,
  className,
  onSelect,
  placeholder = 'Find your station'
}: {
  autoFocus?: boolean;
  isClearable?: boolean;
  isDisabled?: boolean;
  className?: string;
  onSelect?: () => void;
  placeholder?: string;
}) {
  const stationSelectRef = useRef<SelectInstance<StationSelectOption> | null>(null);

  const navigate = useNavigate({ from: '/' });
  const [stationHistory, setStationHistory] = useLocalStorage('MetroBuddy.history', '[]');
  const parsedStationHistory = JSON.parse(stationHistory);
  const stationOptions = Object.entries(STATIONS_BY_CODE).map(([code, station]) => ({
    label: station.Name,
    value: code
  })).sort((a, b) => a.label.localeCompare(b.label));

  const groupedSelectOptions = [
    { label: 'History', options: parsedStationHistory },
    { label: 'All Stations', options: stationOptions }
  ];

  async function onChange(
    newValue: StationSelectOption | null,
    { action }: ActionMeta<StationSelectOption>
  ) {
    if (action === 'select-option' && newValue) {
      await navigate({ to: '/metro/$stationCodes', params: { stationCodes: newValue.value } });
      setStationHistory((prevHistory) => {
        const history = JSON.parse(prevHistory!) as StationSelectOption[];
        if (history.length > 25) {
          history.slice(0, 25);
        }
        const idxOfNewValue = history.findIndex(item => item.value === newValue.value);
        if (idxOfNewValue > -1) {
          history.splice(idxOfNewValue, 1);
        }
        history.unshift(newValue);
        return JSON.stringify(history);
      });
      stationSelectRef.current?.clearValue();
      if (onSelect) {
        onSelect();
      }
    }
  }
  return (
    <EmotionCacheProvider>
      <div className={ className }>
        <Select
          ref={ stationSelectRef }
          autoFocus={ autoFocus }
          className="metro-buddy-react-select-container"
          classNamePrefix="metro-buddy-react-select"
          isClearable={ isClearable }
          isDisabled={ isDisabled }
          options={ parsedStationHistory.length > 0 ? groupedSelectOptions : stationOptions }
          placeholder={ placeholder }
          onChange={ onChange }
        />
      </div>
    </EmotionCacheProvider>
  );
}
