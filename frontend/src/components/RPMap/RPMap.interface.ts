import { Location } from "../../types/Location";

export interface IRPMapProps {
  assets: Location[];
  centerPoint?: Location;
  isLoading?: boolean;
  withSelectLocation?: boolean;
  onSelectLocation?: (location: Location) => void;
}
