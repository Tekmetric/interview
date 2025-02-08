import { render, screen } from '@testing-library/react'
import LaunchList from '../LaunchList'
import { formatDate } from '@/app/lib/utils'
import type { Launch } from '@/app/types'

jest.mock('@/app/lib/utils', () => ({
  formatDate: jest.fn(),
}))

const dummyLaunches: Launch[] = [
  { id: '1', name: 'Launch One', date_utc: '2023-01-01T00:00:00.000Z' },
  { id: '2', name: 'Launch Two', date_utc: '2023-02-01T00:00:00.000Z' },
]

describe('LaunchList', () => {
  beforeEach(() => {
    ;(formatDate as jest.Mock).mockImplementation(
      (date: string) => `Formatted ${date}`
    )
  })

  it('renders no upcoming launches message when launches array is empty', () => {
    render(<LaunchList launches={[]} />)
    expect(screen.getByTestId('no-upcoming-launches')).toHaveTextContent(
      'No upcoming launches scheduled.'
    )
  })

  it('renders a list of upcoming launches', () => {
    render(<LaunchList launches={dummyLaunches} />)
    const list = screen.getByTestId('upcoming-launches-list')
    expect(list).toBeInTheDocument()
    dummyLaunches.forEach((launch) => {
      expect(screen.getByTestId(`launch-item-${launch.id}`)).toBeInTheDocument()
      expect(screen.getByTestId(`launch-name-${launch.id}`)).toHaveTextContent(
        launch.name
      )
      expect(screen.getByTestId(`launch-date-${launch.id}`)).toHaveTextContent(
        `Formatted ${launch.date_utc}`
      )
    })
  })
})
