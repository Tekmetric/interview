import { render, screen } from '@testing-library/react'
import LaunchVideo from '../LaunchVideo'

describe('LaunchVideo', () => {
  it('returns null when youtubeId is undefined', () => {
    const { container } = render(
      <LaunchVideo youtubeId={undefined} launchName="Test Launch" />
    )
    expect(container.firstChild).toBeNull()
  })

  it('renders iframe when youtubeId is provided', () => {
    render(<LaunchVideo youtubeId="abc123" launchName="Test Launch" />)
    const containerEl = screen.getByTestId('launch-video-container')
    expect(containerEl).toBeInTheDocument()
    const iframe = screen.getByTestId('launch-video-iframe')
    expect(iframe).toHaveAttribute(
      'src',
      'https://www.youtube.com/embed/abc123'
    )
    expect(iframe).toHaveAttribute('title', 'Test Launch Launch Video')
  })
})
