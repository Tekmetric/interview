import { render, screen, fireEvent } from '@testing-library/react'
import RocketModal from '../RocketModal'
import type { Rocket } from '@/app/types'

jest.mock('next/image', () => ({
  __esModule: true,
  default: (props: any) => {
    const { src, alt, ...rest } = props
    return <img src={src} alt={alt} {...rest} />
  },
}))

const dummyRocket: Rocket = {
  id: '1',
  name: 'Falcon 9',
  description: 'Test description',
  flickr_images: ['https://example.com/falcon9.jpg'],
  height: { meters: 70, feet: 229.6 },
  diameter: { meters: 3.7, feet: 12.1 },
  mass: { kg: 549054, lb: 1207920 },
  stages: 2,
  boosters: 0,
  first_flight: '2010-06-04',
  company: 'SpaceX',
  country: 'USA',
  cost_per_launch: 50000000,
  success_rate_pct: 97,
  active: true,
  first_stage: {
    engines: 9,
    reusable: true,
    fuel_amount_tons: 385,
    burn_time_sec: 162,
    thrust_sea_level: { kN: 7607 },
    thrust_vacuum: { kN: 8227 },
  },
  second_stage: {
    engines: 1,
    reusable: false,
    fuel_amount_tons: 90,
    burn_time_sec: 397,
    thrust: { kN: 934 },
  },
  engines: {
    type: 'merlin',
    version: '1D+',
    layout: 'v',
    thrust_sea_level: { kN: 845 },
    thrust_vacuum: { kN: 914 },
    propellant_1: 'RP-1',
    propellant_2: 'LOX',
  },
  wikipedia: 'https://en.wikipedia.org/wiki/Falcon_9',
}

describe('RocketModal', () => {
  const onClose = jest.fn()

  beforeEach(() => {
    onClose.mockClear()
    document.body.style.overflow = 'auto'
  })

  it('does not render when isOpen is false', () => {
    render(
      <RocketModal isOpen={false} onClose={onClose} rocket={dummyRocket} />
    )
    expect(screen.queryByTestId('rocket-modal')).toBeNull()
  })

  it('renders modal content when isOpen is true', () => {
    render(<RocketModal isOpen={true} onClose={onClose} rocket={dummyRocket} />)
    const modal = screen.getByTestId('rocket-modal')
    expect(modal).toBeInTheDocument()
    expect(screen.getByTestId('rocket-modal-title')).toHaveTextContent(
      'Falcon 9'
    )
    const image = screen.getByTestId('rocket-image')
    expect(image).toHaveAttribute('src', 'https://example.com/falcon9.jpg')
    expect(image).toHaveAttribute('alt', 'Image of Falcon 9')
    expect(screen.getByTestId('rocket-description')).toHaveTextContent(
      'Test description'
    )
    expect(document.body.style.overflow).toBe('hidden')
  })

  it('calls onClose when Escape key is pressed', () => {
    render(<RocketModal isOpen={true} onClose={onClose} rocket={dummyRocket} />)
    fireEvent.keyDown(document, { key: 'Escape', code: 'Escape' })
    expect(onClose).toHaveBeenCalledTimes(1)
  })

  it('calls onClose when clicking outside modal content', () => {
    render(<RocketModal isOpen={true} onClose={onClose} rocket={dummyRocket} />)
    fireEvent.click(screen.getByTestId('rocket-modal'))
    expect(onClose).toHaveBeenCalledTimes(1)
  })

  it('does not call onClose when clicking inside modal content', () => {
    render(<RocketModal isOpen={true} onClose={onClose} rocket={dummyRocket} />)
    fireEvent.click(screen.getByTestId('rocket-modal-content'))
    expect(onClose).not.toHaveBeenCalled()
  })

  it('calls onClose when the close button is clicked', () => {
    render(<RocketModal isOpen={true} onClose={onClose} rocket={dummyRocket} />)
    fireEvent.click(screen.getByTestId('close-button-1'))
    expect(onClose).toHaveBeenCalledTimes(1)
  })

  it('resets body overflow on unmount', () => {
    const { unmount } = render(
      <RocketModal isOpen={true} onClose={onClose} rocket={dummyRocket} />
    )
    unmount()
    expect(document.body.style.overflow).toBe('auto')
  })
})
