import { render, screen, fireEvent } from '@testing-library/react'
import RocketCard from '../RocketCard'
import type { Rocket } from '@/app/types'

jest.mock('next/image', () => ({
  __esModule: true,
  default: (props: any) => {
    const { src, alt, ...rest } = props
    return <img src={src} alt={alt} {...rest} />
  },
}))

const dummyRocket: Rocket = {
  id: 'rocket1',
  name: 'Falcon 9',
  flickr_images: ['https://example.com/falcon9.jpg'],
  height: { meters: 70, feet: 229.6 },
  diameter: { meters: 3.7, feet: 12.1 },
  mass: { kg: 549054, lb: 1207920 },
  first_flight: '2010-06-04',
  active: true,
}

const dummyRocketNoImage: Rocket = {
  id: 'rocket2',
  name: 'Falcon Heavy',
  flickr_images: [],
  height: { meters: 70, feet: 229.6 },
  diameter: { meters: 12.2, feet: 39.9 },
  mass: { kg: 1420788, lb: 3125735 },
  first_flight: '2018-02-06',
  active: false,
}

describe('RocketCard', () => {
  const onOpenModal = jest.fn()
  beforeEach(() => {
    onOpenModal.mockClear()
  })

  it('renders rocket card with image, name and details', () => {
    render(<RocketCard rocket={dummyRocket} onOpenModal={onOpenModal} />)
    const card = screen.getByTestId('rocket-card')
    expect(card).toBeInTheDocument()
    const image = screen.getByTestId('rocket-image')
    expect(image).toHaveAttribute('src', 'https://example.com/falcon9.jpg')
    expect(image).toHaveAttribute('alt', 'Image of Falcon 9')
    expect(screen.getByTestId('rocket-name')).toHaveTextContent('Falcon 9')
    expect(screen.getByTestId('rocket-detail-Height')).toHaveTextContent(
      'Height: 70m / 229.6ft'
    )
    expect(screen.getByTestId('rocket-detail-Diameter')).toHaveTextContent(
      'Diameter: 3.7m / 12.1ft'
    )
    expect(screen.getByTestId('rocket-detail-Mass')).toHaveTextContent(
      'Mass: 549,054kg / 1,207,920lb'
    )
    expect(screen.getByTestId('rocket-detail-First Flight')).toHaveTextContent(
      'First Flight: 2010-06-04'
    )
    expect(screen.getByTestId('rocket-detail-Status')).toHaveTextContent(
      'Status: Active'
    )
  })

  it('uses placeholder image when no flickr image is available', () => {
    render(<RocketCard rocket={dummyRocketNoImage} onOpenModal={onOpenModal} />)
    const image = screen.getByTestId('rocket-image')
    expect(image).toHaveAttribute('src', '/images/placeholder.svg')
    expect(screen.getByTestId('rocket-name')).toHaveTextContent('Falcon Heavy')
    expect(screen.getByTestId('rocket-detail-Status')).toHaveTextContent(
      'Status: Inactive'
    )
  })

  it('calls onOpenModal when view details button is clicked', () => {
    render(<RocketCard rocket={dummyRocket} onOpenModal={onOpenModal} />)
    const button = screen.getByTestId('view-details-button')
    fireEvent.click(button)
    expect(onOpenModal).toHaveBeenCalledWith(dummyRocket)
  })
})
