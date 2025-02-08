import { render, screen, waitFor } from '@testing-library/react'
import LatestLaunch from '../LatestLaunch'
import type { Launch } from '@/app/types'

const dummyLaunch: Launch = {
  name: 'Test Launch',
  details: 'Test details',
  links: { youtube_id: 'abc123' },
}

describe('LatestLaunch', () => {
  it('renders LoadingSkeleton when latestLaunch is null', () => {
    render(<LatestLaunch latestLaunch={null} />)
    expect(screen.getByTestId('loading-skeleton')).toBeInTheDocument()
  })

  it('renders launch details when latestLaunch is provided', async () => {
    render(<LatestLaunch latestLaunch={dummyLaunch} />)
    expect(screen.getByTestId('latest-launch')).toBeInTheDocument()
    expect(screen.getByTestId('latest-launch-title')).toHaveTextContent(
      'Latest Launch'
    )
    expect(screen.getByTestId('latest-launch-content')).toBeInTheDocument()
    expect(screen.getByTestId('launch-header')).toBeInTheDocument()
    expect(screen.getByTestId('launch-video-details')).toBeInTheDocument()
    await waitFor(() =>
      expect(screen.getByTestId('launch-video-container')).toBeInTheDocument()
    )
    await waitFor(() =>
      expect(screen.getByTestId('launch-video-iframe')).toBeInTheDocument()
    )
    expect(screen.getByTestId('latest-launch-details')).toBeInTheDocument()
    expect(screen.getByTestId('launch-location')).toBeInTheDocument()
    expect(screen.getByTestId('launch-location-title')).toHaveTextContent(
      'Launch Location'
    )
    expect(screen.getByTestId('launch-site-map')).toBeInTheDocument()
  })
})
