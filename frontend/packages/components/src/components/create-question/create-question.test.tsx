import {
  type RenderResult,
  fireEvent,
  render,
  screen
} from '@testing-library/react'

import { CreateQuestion } from './create-question'
import { useCreateQuestion } from './services/use-create-question/use-create-question'

jest.mock('./services/use-create-question/use-create-question', () => ({
  useCreateQuestion: jest.fn()
}))

const useCreateQuestionMock = useCreateQuestion as jest.Mock

const renderComponent = (): RenderResult => render(<CreateQuestion />)

describe('createQuestion', () => {
  beforeEach(() => {
    useCreateQuestionMock.mockReturnValue({
      createQuestion: jest.fn()
    })
  })

  it('calls create question handler', () => {
    const createQuestionMock = jest.fn()
    useCreateQuestionMock.mockReturnValue({
      createQuestion: createQuestionMock
    })

    renderComponent()

    expect(createQuestionMock).not.toHaveBeenCalled()

    fireEvent.change(screen.getByTestId('create-question-title'), {
      target: { value: 'Question Title' }
    })

    fireEvent.change(screen.getByTestId('create-question-description'), {
      target: { value: 'Question Description' }
    })

    fireEvent.click(screen.getByTestId('submit-button'))

    expect(createQuestionMock).toHaveBeenCalledWith(
      {
        title: 'Question Title',
        description: 'Question Description'
      },
      expect.anything(),
      expect.anything()
    )
  })

  it('shows validation errors', () => {
    const createQuestionMock = jest.fn()
    useCreateQuestionMock.mockReturnValue({
      createQuestion: createQuestionMock
    })

    renderComponent()

    expect(createQuestionMock).not.toHaveBeenCalled()

    fireEvent.change(screen.getByTestId('create-question-title'), {
      target: { value: 'a' }
    })

    fireEvent.change(screen.getByTestId('create-question-description'), {
      target: { value: 'b' }
    })

    fireEvent.click(screen.getByTestId('submit-button'))

    expect(createQuestionMock).not.toHaveBeenCalled()

    expect(
      screen.getByText('Title must have at least 5 characters')
    ).toBeInTheDocument()
    expect(
      screen.getByText('Description must have at least 5 characters')
    ).toBeInTheDocument()
  })
})
