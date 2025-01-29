import { render, screen, waitFor } from '@testing-library/react'
import LaunchMap from '../LaunchMap'

jest.mock('../MapLoadingPlaceholder', () => {
  const MockMapLoadingPlaceholder = () => (
    <div data-testid="map-loading-placeholder">Loading Map...</div>
  )
  MockMapLoadingPlaceholder.displayName = 'MockMapLoadingPlaceholder'
  return MockMapLoadingPlaceholder
})

jest.mock('../Map', () => {
  const MockMap = () => <div data-testid="map-component">Map Component</div>
  MockMap.displayName = 'MockMap'
  return MockMap
})

describe('LaunchMap', () => {
  it('renders MapLoadingPlaceholder initially', () => {
    const { getByTestId } = render(<LaunchMap />)
    expect(getByTestId('map-loading-placeholder')).toBeInTheDocument()
  })

  it('renders dynamic map after client hydration', async () => {
    render(<LaunchMap />)
    await waitFor(() =>
      expect(screen.getByTestId('launch-site-map')).toBeInTheDocument()
    )
    expect(screen.getByTestId('map-component')).toBeInTheDocument()
  })
})
