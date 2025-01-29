import { render, screen, waitFor } from '@testing-library/react'
import NextLaunchSection from '../NextLaunchSection'
import { getNextLaunch } from '@/app/lib/api'
import type { Launch } from '@/app/types'

jest.mock('@/app/lib/api', () => ({
  getNextLaunch: jest.fn(),
}))

jest.mock('next/dynamic', () => {
  return (loader: any, options: any) => {
    return function DummyDynamicComponent(props: any) {
      return (
        <div data-testid={props['data-testid']}>{JSON.stringify(props)}</div>
      )
    }
  }
})

describe('NextLaunchSection', () => {
  const dummyLaunch: Launch = {
    id: '1',
    name: 'Next Launch',
    date_utc: '2023-12-31T00:00:00.000Z',
    details: 'Next launch details',
    links: { youtube_id: 'xyz987' },
  }

  beforeEach(() => {
    ;(getNextLaunch as jest.Mock).mockResolvedValue(dummyLaunch)
  })

  it('renders DynamicCountdownTimer with fetched next launch data', async () => {
    const element = await NextLaunchSection()
    render(element)
    await waitFor(() => {
      expect(screen.getByTestId('next-launch-timer')).toBeInTheDocument()
    })
    expect(screen.getByTestId('next-launch-timer')).toHaveTextContent(
      JSON.stringify(dummyLaunch)
    )
  })
})
