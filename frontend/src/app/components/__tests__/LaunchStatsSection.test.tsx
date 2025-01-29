import { render, screen, waitFor } from '@testing-library/react'
import LaunchStatsSection from '../LaunchStatsSection'
import { getLaunchStats } from '@/app/lib/api'
import type { LaunchStats as LaunchStatsType } from '@/app/types'

jest.mock('@/app/lib/api', () => ({
  getLaunchStats: jest.fn(),
}))

jest.mock('../LaunchStats/LaunchStats', () => {
  const LaunchStats = (props: any) => (
    <div data-testid="launch-stats">{JSON.stringify(props.launchStats)}</div>
  )
  LaunchStats.displayName = 'LaunchStats'
  return LaunchStats
})

describe('LaunchStatsSection', () => {
  const dummyStats: LaunchStatsType[] = [
    { year: 2020, launches: 3 },
    { year: 2021, launches: 4 },
  ]

  beforeEach(() => {
    ;(getLaunchStats as jest.Mock).mockResolvedValue(dummyStats)
  })

  it('renders LaunchStats with fetched data', async () => {
    const element = await LaunchStatsSection()
    render(element)
    await waitFor(() => {
      expect(screen.getByTestId('launch-stats')).toBeInTheDocument()
    })
    expect(screen.getByTestId('launch-stats')).toHaveTextContent(
      JSON.stringify(dummyStats)
    )
  })
})
