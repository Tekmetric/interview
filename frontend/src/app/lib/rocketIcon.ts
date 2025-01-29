import { DivIcon } from 'leaflet'
import { RocketIconSVG } from '@/app/components/LaunchMap/RocketIcon'

export function createRocketIcon(): DivIcon {
  return new DivIcon({
    html: RocketIconSVG,
    className: 'rocket-icon',
    iconSize: [24, 24],
    iconAnchor: [48, 48],
  })
}
