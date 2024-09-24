import { Location } from "../types/Location"
import { SightingDTO } from "../types/Sighting"

const buildSighting = (
  pandaId: string | undefined,
  location: Location | undefined,
  dateTime: string | undefined
): SightingDTO => ({
  pandaId: pandaId || "", 
  location, 
  dateTime
})

export const SightingService = {
  buildSighting
}
