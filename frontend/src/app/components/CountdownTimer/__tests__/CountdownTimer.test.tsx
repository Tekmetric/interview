import { render, screen, waitFor } from '@testing-library/react'
import '@testing-library/jest-dom'
import CountdownTimer from '../CountdownTimer'
import * as useCustomDateHook from '../../../hooks/useCustomDate'
import * as useNextLaunchHook from '../../../hooks/useSpaceXData'

jest.mock('../../../hooks/useCustomDate', () => ({
  useCustomDate: jest.fn(),
}))

jest.mock('../../../hooks/useSpaceXData', () => ({
  useNextLaunch: jest.fn(),
}))

jest.mock('framer-motion', () => ({
  motion: {
    div: ({ children, ...props }: React.PropsWithChildren<{}>) => (
      <div {...props}>{children}</div>
    ),
  },
}))

describe('CountdownTimer', () => {
  const mockLaunch = {
    id: '1',
    name: 'Test Launch',
    date_unix: Math.floor(Date.now() / 1000) + 86400,
    date_utc: new Date(Date.now() + 86400000).toISOString(),
    details: 'Test launch details',
    links: {
      patch: {
        small: 'https://example.com/patch-small.png',
        large: 'https://example.com/patch-large.png',
      },
    },
  }

  const mockLaunchpad = {
    id: 'pad1',
    name: 'Test Launchpad',
    full_name: 'Test Launchpad Full Name',
    locality: 'Test City',
    region: 'Test Region',
    latitude: 28.5618571,
    longitude: -80.577366,
  }

  beforeEach(() => {
    jest.useFakeTimers()
    useCustomDateHook.useCustomDate.mockReturnValue(new Date())
    useNextLaunchHook.useNextLaunch.mockReturnValue({
      data: mockLaunch,
      launchpad: mockLaunchpad,
      error: null,
    })
  })

  afterEach(() => {
    jest.useRealTimers()
    jest.clearAllMocks()
  })

  it('renders without crashing', () => {
    render(<CountdownTimer initialData={mockLaunch} />)
    expect(screen.getByText('Next SpaceX Launch')).toBeInTheDocument()
  })

  it('displays countdown timer', () => {
    render(<CountdownTimer initialData={mockLaunch} />)
    expect(screen.getByText('days')).toBeInTheDocument()
    expect(screen.getByText('hours')).toBeInTheDocument()
    expect(screen.getByText('minutes')).toBeInTheDocument()
    expect(screen.getByText('seconds')).toBeInTheDocument()
  })

  it('displays error message when there is an error', () => {
    useNextLaunchHook.useNextLaunch.mockReturnValue({
      data: null,
      launchpad: null,
      error: new Error('Failed to fetch launch data'),
    })

    render(<CountdownTimer initialData={mockLaunch} />)
    expect(screen.getByText('Failed to fetch launch data')).toBeInTheDocument()
  })

  it('displays loading skeleton when launch data is not available', () => {
    useNextLaunchHook.useNextLaunch.mockReturnValue({
      data: null,
      launchpad: null,
      error: null,
    })

    render(<CountdownTimer initialData={null} />)
    expect(screen.getByTestId('loading-skeleton')).toBeInTheDocument()
  })

  it('handles missing launchpad data gracefully', async () => {
    useNextLaunchHook.useNextLaunch.mockReturnValue({
      data: mockLaunch,
      launchpad: null,
      error: null,
    })

    render(<CountdownTimer initialData={mockLaunch} />)

    await waitFor(() => {
      expect(screen.getByText('Test Launch')).toBeInTheDocument()
      expect(
        screen.queryByText('Test Launchpad Full Name')
      ).not.toBeInTheDocument()
    })
  })

  it('resets timer when it reaches zero', () => {
    const pastLaunch = {
      ...mockLaunch,
      date_unix: Math.floor(Date.now() / 1000) - 1,
    }

    useNextLaunchHook.useNextLaunch.mockReturnValue({
      data: pastLaunch,
      launchpad: mockLaunchpad,
      error: null,
    })

    render(<CountdownTimer initialData={pastLaunch} />)

    const days = screen.getAllByText('00')[0]
    const hours = screen.getAllByText('00')[1]
    const minutes = screen.getAllByText('00')[2]
    const seconds = screen.getAllByText('00')[3]

    expect(days.nextSibling).toHaveTextContent('days')
    expect(hours.nextSibling).toHaveTextContent('hours')
    expect(minutes.nextSibling).toHaveTextContent('minutes')
    expect(seconds.nextSibling).toHaveTextContent('seconds')
  })
})
