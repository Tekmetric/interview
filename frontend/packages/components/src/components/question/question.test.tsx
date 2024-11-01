import { QuestionStatus, useQuery } from '@tekmetric/graphql'
import { type RenderResult, render, screen } from '@testing-library/react'

import { Question } from './question'

jest.mock('@tekmetric/graphql', () => ({
  useQuery: jest.fn(),
  GetQuestionDocument: {},
  QuestionStatus: {
    Pending: 'PENDING',
    Resolved: 'RESOLVED'
  }
}))

jest.mock('../base-question/base-question', () => ({
  BaseQuestion: jest.fn(() => <div>Mocked BaseQuestion</div>)
}))

jest.mock('../cards-skeleton-loader/cards-skeleton-loader', () => ({
  CardsSkeletonLoader: jest.fn(() => <div>Mocked CardsSkeletonLoader</div>)
}))

jest.mock('./components/answers/answers', () => ({
  Answers: jest.fn(() => <div>Mocked Answers</div>)
}))

jest.mock('./components/create-answer/create-answer', () => ({
  CreateAnswer: jest.fn(() => <div>Mocked CreateAnswer</div>)
}))

const useQueryMock = useQuery as jest.Mock

const questionId = '1'
const renderComponent = (): RenderResult =>
  render(<Question questionId={questionId} />)

describe('Question', () => {
  it('should render loading state', () => {
    useQueryMock.mockReturnValue({ data: null, loading: true })

    renderComponent()

    expect(screen.getByText('Mocked CardsSkeletonLoader')).toBeInTheDocument()
  })

  it('should render no data state', () => {
    useQueryMock.mockReturnValue({ data: null, loading: false })

    renderComponent()

    expect(
      screen.getByText('There is no question to display.')
    ).toBeInTheDocument()
  })

  it('should render question data', () => {
    useQueryMock.mockReturnValue({
      data: {
        question: {
          id: questionId,
          title: 'Sample Question',
          status: QuestionStatus.Pending,
          answers: []
        }
      },
      loading: false
    })

    renderComponent()

    expect(screen.getByText('Mocked BaseQuestion')).toBeInTheDocument()
    expect(screen.getByText('Mocked Answers')).toBeInTheDocument()
    expect(screen.getByText('Mocked CreateAnswer')).toBeInTheDocument()
  })

  it('should not render CreateAnswer if question is not pending', () => {
    useQueryMock.mockReturnValue({
      data: {
        question: {
          id: questionId,
          title: 'Sample Question',
          status: QuestionStatus.Completed,
          answers: []
        }
      },
      loading: false
    })

    renderComponent()

    expect(screen.getByText('Mocked BaseQuestion')).toBeInTheDocument()
    expect(screen.getByText('Mocked Answers')).toBeInTheDocument()
    expect(screen.queryByText('Mocked CreateAnswer')).not.toBeInTheDocument()
  })
})
