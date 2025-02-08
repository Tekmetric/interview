import { render, screen, waitFor } from '@testing-library/react'
import LatestLaunchSection from '../LatestLaunchSection'
import { getLatestLaunch } from '@/app/lib/api'
import type { Launch } from '@/app/types'

jest.mock('@/app/lib/api', () => ({
  getLatestLaunch: jest.fn(),
}))
jest.mock('../LatestLaunch/LatestLaunch', () => {
  const MockLatestLaunch = (props: any) => (
    <div data-testid="latest-launch">{JSON.stringify(props.latestLaunch)}</div>
  )
  MockLatestLaunch.displayName = 'MockLatestLaunch'
  return MockLatestLaunch
})

describe('LatestLaunchSection', () => {
  const dummyLaunch: Launch = {
    id: '1',
    name: 'Test Launch',
    date_utc: '2023-01-01T00:00:00.000Z',
    details: 'Test details',
    links: { youtube_id: 'abc123' },
  }

  beforeEach(() => {
    ;(getLatestLaunch as jest.Mock).mockResolvedValue(dummyLaunch)
  })

  it('renders LatestLaunch with fetched data', async () => {
    const element = await LatestLaunchSection()
    render(element)
    await waitFor(() =>
      expect(screen.getByTestId('latest-launch')).toBeInTheDocument()
    )
    expect(screen.getByTestId('latest-launch')).toHaveTextContent(
      JSON.stringify(dummyLaunch)
    )
  })
})
