import { render, screen, fireEvent } from '@testing-library/react'
import RocketDetails from '../RocketDetails'
import type { Rocket } from '@/app/types'

jest.mock('../RocketModal', () => {
  const MockRocketModal = (props: any) => {
    if (!props.isOpen) return null
    return <div data-testid="rocket-modal">{props.rocket.name}</div>
  }
  MockRocketModal.displayName = 'MockRocketModal'
  return MockRocketModal
})

jest.mock('../RocketCard', () => {
  const MockRocketCard = (props: any) => {
    return (
      <div data-testid="rocket-card">
        <div>{props.rocket.name}</div>
        <button
          data-testid="view-details-button"
          onClick={() => props.onOpenModal(props.rocket)}
        >
          View Details
        </button>
      </div>
    )
  }
  MockRocketCard.displayName = 'MockRocketCard'
  return MockRocketCard
})

const dummyRockets: Rocket[] = [
  {
    id: '1',
    name: 'Falcon 9',
    flickr_images: ['https://example.com/falcon9.jpg'],
    height: { meters: 70, feet: 229.6 },
    diameter: { meters: 3.7, feet: 12.1 },
    mass: { kg: 549054, lb: 1207920 },
    description: 'Test description',
    first_flight: '2010-06-04',
    active: true,
    first_stage: {
      engines: 9,
      reusable: true,
      fuel_amount_tons: 385,
      burn_time_sec: 162,
      thrust_sea_level: { kN: 7607 },
      thrust_vacuum: { kN: 8227 },
    },
    second_stage: {
      engines: 1,
      reusable: false,
      fuel_amount_tons: 90,
      burn_time_sec: 397,
      thrust: { kN: 934 },
    },
    engines: {
      type: 'merlin',
      version: '1D+',
      layout: 'v',
      thrust_sea_level: { kN: 845 },
      thrust_vacuum: { kN: 914 },
    },
    diameter: { meters: 3.7, feet: 12.1 },
    mass: { kg: 549054, lb: 1207920 },
    stages: 2,
    boosters: 0,
    company: 'SpaceX',
    country: 'USA',
    cost_per_launch: 50000000,
    success_rate_pct: 97,
  },
  {
    id: '2',
    name: 'Falcon Heavy',
    flickr_images: ['https://example.com/falconheavy.jpg'],
    height: { meters: 70, feet: 229.6 },
    diameter: { meters: 12.2, feet: 39.9 },
    mass: { kg: 1420788, lb: 3125735 },
    description: 'Test description',
    first_flight: '2018-02-06',
    active: false,
    first_stage: {
      engines: 9,
      reusable: true,
      fuel_amount_tons: 385,
      burn_time_sec: 162,
      thrust_sea_level: { kN: 7607 },
      thrust_vacuum: { kN: 8227 },
    },
    second_stage: {
      engines: 1,
      reusable: false,
      fuel_amount_tons: 90,
      burn_time_sec: 397,
      thrust: { kN: 934 },
    },
    engines: {
      type: 'merlin',
      version: '1D+',
      layout: 'v',
      thrust_sea_level: { kN: 845 },
      thrust_vacuum: { kN: 914 },
    },
    diameter: { meters: 12.2, feet: 39.9 },
    mass: { kg: 1420788, lb: 3125735 },
    stages: 2,
    boosters: 0,
    company: 'SpaceX',
    country: 'USA',
    cost_per_launch: 50000000,
    success_rate_pct: 97,
  },
]

beforeAll(() => {
  class FakeIntersectionObserver {
    observe() {}
    unobserve() {}
    disconnect() {}
  }
  Object.defineProperty(window, 'IntersectionObserver', {
    writable: true,
    configurable: true,
    value: FakeIntersectionObserver,
  })
})

describe('RocketDetails', () => {
  it('renders rocket details section with title and grid', () => {
    render(<RocketDetails rockets={dummyRockets} />)
    expect(screen.getByTestId('rocket-details-section')).toBeInTheDocument()
    expect(screen.getByTestId('rocket-details-title')).toHaveTextContent(
      'SpaceX Rockets'
    )
    expect(screen.getByTestId('rocket-details-grid')).toBeInTheDocument()
    const rocketCards = screen.getAllByTestId('rocket-card')
    expect(rocketCards.length).toBe(dummyRockets.length)
  })

  it('opens modal when view details button is clicked', () => {
    render(<RocketDetails rockets={dummyRockets} />)
    const viewButtons = screen.getAllByTestId('view-details-button')
    fireEvent.click(viewButtons[0])
    const modal = screen.getByTestId('rocket-modal')
    expect(modal).toBeInTheDocument()
    expect(modal).toHaveTextContent('Falcon 9')
  })
})
