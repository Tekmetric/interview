import { render, screen } from '@testing-library/react'
import LaunchHeader from '../LaunchHeader'
import { formatDate } from '@/app/lib/utils'
jest.mock('next/image', () => ({
  __esModule: true,
  default: (props: any) => <img alt="" {...props} />,
}))
jest.mock('@/app/lib/utils', () => ({
  formatDate: jest.fn(),
}))

const mockedFormatDate = formatDate as jest.Mock

describe('LaunchHeader', () => {
  beforeEach(() => {
    mockedFormatDate.mockReturnValue('January 1, 2021')
  })

  const dummyLaunchWithPatch = {
    name: 'Test Launch',
    date_utc: '2021-01-01T00:00:00.000Z',
    success: true,
    links: {
      patch: {
        small: 'https://example.com/patch.png',
      },
    },
  }

  const dummyLaunchWithoutPatch = {
    name: 'Test Launch No Patch',
    date_utc: '2021-01-02T00:00:00.000Z',
    success: false,
    links: {
      patch: {
        small: null,
      },
    },
  }

  it('renders mission patch when available', () => {
    render(<LaunchHeader launch={dummyLaunchWithPatch} />)
    expect(screen.getByTestId('launch-header')).toBeInTheDocument()
    expect(screen.getByTestId('mission-patch')).toBeInTheDocument()
    expect(screen.getByTestId('launch-name')).toHaveTextContent('Test Launch')
    expect(screen.getByTestId('launch-date')).toHaveTextContent(
      'January 1, 2021'
    )
    expect(screen.getByTestId('launch-status')).toHaveTextContent('Successful')
  })

  it('does not render mission patch when not available', () => {
    render(<LaunchHeader launch={dummyLaunchWithoutPatch} />)
    expect(screen.getByTestId('launch-header')).toBeInTheDocument()
    expect(screen.queryByTestId('mission-patch')).toBeNull()
    expect(screen.getByTestId('launch-name')).toHaveTextContent(
      'Test Launch No Patch'
    )
    expect(screen.getByTestId('launch-date')).toHaveTextContent(
      'January 1, 2021'
    )
    expect(screen.getByTestId('launch-status')).toHaveTextContent('Failed')
  })
})
