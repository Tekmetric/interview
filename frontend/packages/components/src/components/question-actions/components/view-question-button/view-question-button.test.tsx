import {
  type RenderResult,
  fireEvent,
  render,
  screen
} from '@testing-library/react'
import { useRouter } from 'next/navigation'

import { Routes } from '../../../../enums/routes'
import { ViewQuestionButton } from './view-question-button'

jest.mock('next/navigation', () => ({
  useRouter: jest.fn()
}))

const questionId = '1'
const renderComponent = (): RenderResult =>
  render(<ViewQuestionButton questionId={questionId}>View</ViewQuestionButton>)

describe('ViewQuestionButton', () => {
  const mockUseRouter = useRouter as jest.Mock

  beforeEach(() => {
    mockUseRouter.mockClear()
  })

  it('navigates to the correct route on button click', () => {
    const mockPush = jest.fn()
    mockUseRouter.mockReturnValue({ push: mockPush })

    renderComponent()

    fireEvent.click(screen.getByText('View'))

    expect(mockPush).toHaveBeenCalledWith(`${Routes.Dashboard}/${questionId}`)
  })
})
