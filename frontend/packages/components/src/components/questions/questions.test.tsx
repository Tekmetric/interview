import { QuestionStatus, useQuery } from '@tekmetric/graphql'
import { render, screen } from '@testing-library/react'
import { usePathname } from 'next/navigation'

import { Questions } from './questions'

// eslint-disable-next-line @typescript-eslint/no-unsafe-return -- jest mock
jest.mock('@tekmetric/graphql', () => ({
  ...jest.requireActual('@tekmetric/graphql'),
  useQuery: jest.fn(),
  GetQuestionsDocument: {}
}))

jest.mock('next/navigation', () => ({
  usePathname: jest.fn()
}))

jest.mock('../base-question/base-question', () => ({
  BaseQuestion: jest.fn(() => <div>Mocked BaseQuestion</div>)
}))

jest.mock('../cards-skeleton-loader/cards-skeleton-loader', () => ({
  CardsSkeletonLoader: jest.fn(() => <div>Mocked CardsSkeletonLoader</div>)
}))

describe('Questions', () => {
  const useQueryMock = useQuery as jest.Mock
  const usePathnameMock = usePathname as jest.Mock
  const status = QuestionStatus.Pending

  beforeEach(() => {
    useQueryMock.mockClear()
    usePathnameMock.mockClear()
  })

  it('renders loading state', () => {
    useQueryMock.mockReturnValue({
      data: null,
      loading: true,
      refetch: jest.fn()
    })
    usePathnameMock.mockReturnValue('/path')

    render(<Questions status={status} />)

    expect(screen.getByText('Mocked CardsSkeletonLoader')).toBeInTheDocument()
  })

  it('renders no data state', () => {
    useQueryMock.mockReturnValue({
      data: null,
      loading: false,
      refetch: jest.fn()
    })
    usePathnameMock.mockReturnValue('/path')

    render(<Questions status={status} />)

    expect(
      screen.getByText('There are no questions to display.')
    ).toBeInTheDocument()
  })

  it('renders questions data', () => {
    const questions = [
      { id: '1', title: 'Question 1' },
      { id: '2', title: 'Question 2' }
    ]

    useQueryMock.mockReturnValue({
      data: { questions },
      loading: false,
      refetch: jest.fn()
    })
    usePathnameMock.mockReturnValue('/path')

    render(<Questions status={status} />)

    expect(screen.getAllByText('Mocked BaseQuestion')).toHaveLength(2)
  })

  it('refetch data when pathname changes', () => {
    const refetch = jest.fn()
    useQueryMock.mockReturnValue({ data: null, loading: false, refetch })
    usePathnameMock.mockReturnValue('/path')

    const { rerender } = render(<Questions status={status} />)

    usePathnameMock.mockReturnValue('/new-path')
    rerender(<Questions status={status} />)

    expect(refetch).toHaveBeenCalledTimes(2)
  })
})
