import { render, screen, fireEvent } from '@testing-library/react'
import LaunchList from '../LaunchList'
import type { Launch } from '@/app/types'

jest.mock('@/app/lib/utils', () => ({
  formatDate: (date: string) => `Formatted: ${date}`,
}))

const dummyLaunches: Launch[] = [
  {
    id: '1',
    name: 'Launch 1',
    date_utc: '2021-01-01T00:00:00.000Z',
    success: true,
  },
  {
    id: '2',
    name: 'Launch 2',
    date_utc: '2021-02-01T00:00:00.000Z',
    success: false,
  },
]

describe('LaunchList', () => {
  it('renders no launches message when launches array is empty', () => {
    render(
      <LaunchList
        launches={[]}
        hasMoreToLoad={false}
        isLoadingMore={false}
        onLoadMore={jest.fn()}
      />
    )
    const noLaunchesMessage = screen.getByTestId('no-launches-message')
    expect(noLaunchesMessage).toBeInTheDocument()
    expect(noLaunchesMessage).toHaveTextContent('No launches found')
  })

  it("renders launch items correctly and shows 'All launches loaded' when hasMoreToLoad is false", () => {
    render(
      <LaunchList
        launches={dummyLaunches}
        hasMoreToLoad={false}
        isLoadingMore={false}
        onLoadMore={jest.fn()}
      />
    )
    const list = screen.getByTestId('launch-list')
    expect(list).toBeInTheDocument()
    dummyLaunches.forEach((launch) => {
      const item = screen.getByTestId(`launch-item-${launch.id}`)
      expect(item).toBeInTheDocument()
      expect(item).toHaveTextContent(launch.name)
      expect(item).toHaveTextContent(`Formatted: ${launch.date_utc}`)
      expect(item).toHaveTextContent(launch.success ? 'Success' : 'Failure')
    })
    expect(screen.getByTestId('all-launches-loaded')).toBeInTheDocument()
  })

  it('renders load more button when hasMoreToLoad is true and not loading', () => {
    const onLoadMore = jest.fn()
    render(
      <LaunchList
        launches={dummyLaunches}
        hasMoreToLoad={true}
        isLoadingMore={false}
        onLoadMore={onLoadMore}
      />
    )
    const loadMoreButton = screen.getByTestId('load-more-button')
    expect(loadMoreButton).toBeInTheDocument()
    expect(loadMoreButton).toHaveTextContent('Load More')
    fireEvent.click(loadMoreButton)
    expect(onLoadMore).toHaveBeenCalledTimes(1)
  })

  it("renders load more button as disabled with 'Loading...' text when isLoadingMore is true", () => {
    render(
      <LaunchList
        launches={dummyLaunches}
        hasMoreToLoad={true}
        isLoadingMore={true}
        onLoadMore={jest.fn()}
      />
    )
    const loadMoreButton = screen.getByTestId('load-more-button')
    expect(loadMoreButton).toBeDisabled()
    expect(loadMoreButton).toHaveTextContent('Loading...')
  })
})
