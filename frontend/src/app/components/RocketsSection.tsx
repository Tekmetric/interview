import { getRockets } from '@/app/lib/api'
import type { Rocket } from '@/app/types'

import RocketDetails from './RocketDetails/RocketDetails'

async function RocketsSection(): Promise<React.ReactElement> {
  const rocketsData = (await getRockets()) as Rocket[]

  return <RocketDetails rockets={rocketsData} data-testid="rockets-details" />
}

export default RocketsSection
