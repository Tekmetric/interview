import { QuestionStatus } from '@tekmetric/graphql'
import { render, screen } from '@testing-library/react'

import { QuestionActions } from './question-actions'

jest.mock('./components/resolve-button/resolve-button', () => ({
  ResolveButton: jest.fn(() => (
    <button type='button'>Mocked ResolveButton</button>
  ))
}))

jest.mock('./components/view-question-button/view-question-button', () => ({
  ViewQuestionButton: jest.fn(({ children }) => (
    <button type='button'>{children}</button>
  ))
}))

jest.mock('@tekmetric/ui/icon', () => ({
  Icon: jest.fn(({ icon }) => <span>{icon}</span>)
}))

describe('QuestionActions', () => {
  const questionId = '1'

  it('renders ResolveButton when status is pending and canResolve is true', () => {
    render(
      <QuestionActions
        questionId={questionId}
        status={QuestionStatus.Pending}
        canResolve
        hideViewButton={false}
      />
    )

    expect(screen.getByText('Mocked ResolveButton')).toBeInTheDocument()
  })

  it('renders ViewQuestionButton with "View" text when status is not pending and hideViewButton is false', () => {
    render(
      <QuestionActions
        questionId={questionId}
        status={QuestionStatus.Completed}
        canResolve={false}
        hideViewButton={false}
      />
    )

    expect(screen.getByText('View')).toBeInTheDocument()
  })

  it('renders ViewQuestionButton with "Answer" text and chat icon when status is pending and hideViewButton is false', () => {
    render(
      <QuestionActions
        questionId={questionId}
        status={QuestionStatus.Pending}
        canResolve={false}
        hideViewButton={false}
      />
    )

    expect(screen.getByText('Answer')).toBeInTheDocument()
    expect(screen.getByText('chat')).toBeInTheDocument()
  })

  it('does not render ViewQuestionButton when hideViewButton is true', () => {
    render(
      <QuestionActions
        questionId={questionId}
        status={QuestionStatus.Pending}
        canResolve={false}
        hideViewButton
      />
    )

    expect(screen.queryByText('View')).not.toBeInTheDocument()
    expect(screen.queryByText('Answer')).not.toBeInTheDocument()
  })
})
