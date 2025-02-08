import { render, screen } from '@testing-library/react'
import { LaunchInfo } from '../LaunchInfo'

describe('LaunchInfo', () => {
  const mockLaunch = {
    name: 'Test Launch',
    date_utc: '2023-10-01T00:00:00Z',
    links: {
      webcast: 'http://example.com/webcast',
    },
  }

  const mockLaunchpad = {
    full_name: 'Test Launchpad',
  }

  it('renders launch information correctly', () => {
    render(<LaunchInfo launch={mockLaunch} launchpad={mockLaunchpad} />)

    expect(screen.getByText(/mission/i)).toBeInTheDocument()
    expect(screen.getByText(mockLaunch.name)).toBeInTheDocument()
    expect(screen.getByText(/launch site/i)).toBeInTheDocument()
    expect(screen.getByText(mockLaunchpad.full_name)).toBeInTheDocument()
    expect(screen.getByText(/launch date/i)).toBeInTheDocument()
  })

  it('renders loading state for launchpad', () => {
    render(<LaunchInfo launch={mockLaunch} launchpad={null} />)

    expect(screen.getByText(/loading launch site/i)).toBeInTheDocument()
  })

  it('renders the webcast link when available', () => {
    render(<LaunchInfo launch={mockLaunch} launchpad={mockLaunchpad} />)

    const link = screen.getByRole('link', { name: /watch webcast/i })
    expect(link).toHaveAttribute('href', mockLaunch.links.webcast)
  })
})
