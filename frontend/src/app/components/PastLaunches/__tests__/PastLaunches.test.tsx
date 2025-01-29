import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import PastLaunches from '../PastLaunches'
import { usePastLaunches } from '@/app/hooks/useSpaceXData'
import { useCustomDate } from '@/app/hooks/useCustomDate'

jest.mock('@/app/hooks/useSpaceXData')
jest.mock('@/app/hooks/useCustomDate')

const dummyLaunch = (id: number) => ({
  id: `${id}`,
  name: `Launch ${id}`,
  date_utc: '2021-01-01T00:00:00.000Z',
  success: id % 2 === 0,
  rocket: 'rocket-id',
})
const dummyLaunches = Array.from({ length: 6 }, (_, i) => dummyLaunch(i + 1))

describe('PastLaunches', () => {
  const setSizeMock = jest.fn()
  beforeEach(() => {
    jest.clearAllMocks()
  })

  it('renders error message when error exists', () => {
    ;(usePastLaunches as jest.Mock).mockReturnValue({
      data: null,
      size: 1,
      setSize: setSizeMock,
      isValidating: false,
      error: new Error('Error'),
    })
    ;(useCustomDate as jest.Mock).mockReturnValue('2022-01-01T00:00:00.000Z')
    render(<PastLaunches initialData={[]} />)
    expect(screen.getByText('Failed to load past launches')).toBeInTheDocument()
  })

  it('renders loading skeleton when data is null', () => {
    ;(usePastLaunches as jest.Mock).mockReturnValue({
      data: null,
      size: 1,
      setSize: setSizeMock,
      isValidating: false,
      error: null,
    })
    ;(useCustomDate as jest.Mock).mockReturnValue('2022-01-01T00:00:00.000Z')
    render(<PastLaunches initialData={dummyLaunches} />)
    expect(screen.getByTestId('loading-skeleton')).toBeInTheDocument()
  })

  it('renders past launches section with title, filters, and list', async () => {
    ;(usePastLaunches as jest.Mock).mockReturnValue({
      data: [dummyLaunches],
      size: 1,
      setSize: setSizeMock,
      isValidating: false,
      error: null,
    })
    ;(useCustomDate as jest.Mock).mockReturnValue('2022-01-01T00:00:00.000Z')
    render(<PastLaunches initialData={dummyLaunches} />)
    expect(screen.getByTestId('past-launches-section')).toBeInTheDocument()
    expect(screen.getByTestId('past-launches-title')).toHaveTextContent(
      'Past Launches'
    )
    expect(screen.getByTestId('launch-filters')).toBeInTheDocument()
    expect(screen.getByTestId('launch-list')).toBeInTheDocument()
  })

  it('calls setSize when load more button is clicked', async () => {
    ;(usePastLaunches as jest.Mock).mockReturnValue({
      data: [dummyLaunches],
      size: 1,
      setSize: setSizeMock,
      isValidating: false,
      error: null,
    })
    ;(useCustomDate as jest.Mock).mockReturnValue('2022-01-01T00:00:00.000Z')
    render(<PastLaunches initialData={dummyLaunches} />)
    await waitFor(() =>
      expect(screen.getByTestId('load-more-button')).toBeInTheDocument()
    )
    const button = screen.getByTestId('load-more-button')
    fireEvent.click(button)
    expect(setSizeMock).toHaveBeenCalledTimes(1)
  })
})
