import type { StationSelectOption } from '@customTypes/components/StationSelect';
import type { ActionMeta, SelectInstance } from 'react-select';
import { STATIONS_BY_CODE } from '@constants/wmata';
import { useNavigate } from '@tanstack/react-router';
import { useLocalStorage } from '@uidotdev/usehooks';
import { useRef } from 'react';
import Select from 'react-select';

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
    <div className={ className }>
      <Select
        ref={ stationSelectRef }
        autoFocus={ autoFocus }
        className="text-left mx-auto"
        isClearable={ isClearable }
        isDisabled={ isDisabled }
        options={ parsedStationHistory.length > 0 ? groupedSelectOptions : stationOptions }
        placeholder={ placeholder }
        onChange={ onChange }
      />
    </div>
  );
}
