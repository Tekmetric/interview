import { render, screen, waitFor } from '@testing-library/react'
import RocketsSection from '../RocketsSection'
import { getRockets } from '@/app/lib/api'
import type { Rocket } from '@/app/types'

jest.mock('@/app/lib/api', () => ({
  getRockets: jest.fn(),
}))

jest.mock('../RocketDetails/RocketDetails', () => {
  const RocketDetails = (props: any) => (
    <div data-testid="rockets-details">{JSON.stringify(props.rockets)}</div>
  )
  RocketDetails.displayName = 'RocketDetails'
  return RocketDetails
})

describe('RocketsSection', () => {
  const dummyRockets: Rocket[] = [
    { id: '1', name: 'Falcon 9' } as Rocket,
    { id: '2', name: 'Falcon Heavy' } as Rocket,
  ]

  beforeEach(() => {
    ;(getRockets as jest.Mock).mockResolvedValue(dummyRockets)
  })

  it('renders RocketDetails with fetched rockets data', async () => {
    const element = await RocketsSection()
    render(element)
    await waitFor(() =>
      expect(screen.getByTestId('rockets-details')).toBeInTheDocument()
    )
    expect(screen.getByTestId('rockets-details')).toHaveTextContent(
      JSON.stringify(dummyRockets)
    )
  })
})
