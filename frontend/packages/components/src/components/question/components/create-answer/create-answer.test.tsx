import {
  type RenderResult,
  fireEvent,
  render,
  screen
} from '@testing-library/react'

import { CreateAnswer } from './create-answer'
import { useCreateAnswer } from './services/use-create-answer/use-create-answer'

jest.mock('./services/use-create-answer/use-create-answer', () => ({
  useCreateAnswer: jest.fn()
}))

const useCreateAnswerMock = useCreateAnswer as jest.Mock

const questionId = '1'
const renderComponent = (): RenderResult =>
  render(<CreateAnswer questionId={questionId} />)

describe('CreateAnswer', () => {
  beforeEach(() => {
    useCreateAnswerMock.mockReturnValue({
      createAnswer: jest.fn()
    })
  })

  it('calls create answer handler', () => {
    const createAnswerMock = jest.fn()
    useCreateAnswerMock.mockReturnValue({
      createAnswer: createAnswerMock
    })

    renderComponent()

    expect(createAnswerMock).not.toHaveBeenCalled()

    fireEvent.change(screen.getByTestId('create-answer-description'), {
      target: { value: 'Answer Description' }
    })

    fireEvent.click(screen.getByTestId('submit-button'))

    expect(createAnswerMock).toHaveBeenCalledWith(
      {
        description: 'Answer Description'
      },
      expect.anything(),
      expect.anything()
    )
  })

  it('shows validation errors', () => {
    const createAnswerMock = jest.fn()
    useCreateAnswerMock.mockReturnValue({
      createAnswer: createAnswerMock
    })

    renderComponent()

    expect(createAnswerMock).not.toHaveBeenCalled()

    fireEvent.change(screen.getByTestId('create-answer-description'), {
      target: { value: 'b' }
    })

    fireEvent.click(screen.getByTestId('submit-button'))

    expect(createAnswerMock).not.toHaveBeenCalled()

    expect(
      screen.getByText('Description must have at least 5 characters')
    ).toBeInTheDocument()
  })
})
