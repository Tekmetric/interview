import { StationSelect } from '@components/StationSelect';
import { useKBarShortcut } from '@hooks/useKBarShortcut';

export function StationSelectKBar() {
  const [showStationSelect, setShowStationSelect] = useKBarShortcut();

  return showStationSelect
    ? (
        <div className="absolute mx-auto my-auto w-full h-full bg-gray-500/80 z-10000">
          <div className="flex justify-center">
            <div className="absolute top-1/3 w-[60%]">
              <StationSelect
                className="text-3xl"
                onSelect={ () => setShowStationSelect(false) }
              />
            </div>
          </div>
        </div>
      )
    : '';
}
