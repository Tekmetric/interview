import { render, screen, waitFor } from '@testing-library/react'
import LaunchesSection from '../LaunchesSection'
import { getUpcomingLaunches, getPastLaunches } from '@/app/lib/api'
import type { Launch } from '@/app/types'

jest.mock('@/app/lib/api', () => ({
  getUpcomingLaunches: jest.fn(),
  getPastLaunches: jest.fn(),
}))

// Mock next/dynamic to return a dummy functional component that simply renders its props.
// This ensures that our dynamic components render synchronously in the test.
jest.mock('next/dynamic', () => {
  return (loader: any, options: any) => {
    return function DummyDynamicComponent(props: any) {
      return (
        <div data-testid={props['data-testid']}>{JSON.stringify(props)}</div>
      )
    }
  }
})

describe('LaunchesSection', () => {
  const dummyUpcomingLaunches: Launch[] = [
    {
      id: '1',
      name: 'Upcoming Launch 1',
      date_utc: '2023-12-31T00:00:00.000Z',
    } as Launch,
  ]
  const dummyPastLaunches: Launch[] = [
    {
      id: '2',
      name: 'Past Launch 1',
      date_utc: '2023-01-01T00:00:00.000Z',
    } as Launch,
  ]

  beforeEach(() => {
    ;(getUpcomingLaunches as jest.Mock).mockResolvedValue(dummyUpcomingLaunches)
    ;(getPastLaunches as jest.Mock).mockResolvedValue(dummyPastLaunches)
  })

  it('renders dynamic upcoming and past launches components with fetched data', async () => {
    const element = await LaunchesSection()
    render(element)

    await waitFor(() => {
      expect(
        screen.getByTestId('dynamic-upcoming-launches')
      ).toBeInTheDocument()
      expect(screen.getByTestId('dynamic-past-launches')).toBeInTheDocument()
    })
  })
})
