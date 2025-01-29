import { render, screen } from '@testing-library/react'
import LaunchStats from '../LaunchStats'
import type { LaunchStats as LaunchStatsType } from '@/app/types'

jest.mock('../LaunchStatsChart', () => {
  return function LaunchStatsChartMock(props: any) {
    return (
      <div data-testid={props['data-testid']}>{JSON.stringify(props.data)}</div>
    )
  }
})

describe('LaunchStats', () => {
  const dummyLaunchStats: LaunchStatsType[] = [
    { year: 2020, launches: 3 },
    { year: 2021, launches: 4 },
  ]

  it('renders section and title', () => {
    render(<LaunchStats launchStats={dummyLaunchStats} />)
    expect(screen.getByTestId('launch-stats-section')).toBeInTheDocument()
    expect(screen.getByText('Launch Statistics')).toBeInTheDocument()
  })

  it('renders LaunchStatsChart with provided data', () => {
    render(<LaunchStats launchStats={dummyLaunchStats} />)
    const chart = screen.getByTestId('launch-stats-chart')
    expect(chart).toBeInTheDocument()
    expect(chart).toHaveTextContent(JSON.stringify(dummyLaunchStats))
  })
})
