import { useMutation } from '@tekmetric/graphql'
import { act, renderHook } from '@testing-library/react'
import { useRouter } from 'next/navigation'

import { Routes } from '../../../../enums/routes'
import { useToastContainer } from '../../../../services/use-toast-container/use-toast-container'
import { useCreateQuestion } from './use-create-question'

jest.mock('@tekmetric/graphql', () => ({
  useMutation: jest.fn(),
  CreateQuestionDocument: {},
  GetQuestionsDocument: {}
}))

jest.mock('next/navigation', () => ({
  useRouter: jest.fn()
}))

jest.mock(
  '../../../../services/use-toast-container/use-toast-container',
  () => ({
    useToastContainer: jest.fn()
  })
)

describe('useCreateQuestion', () => {
  const mockUseMutation = useMutation as jest.Mock
  const mockUseRouter = useRouter as jest.Mock
  const mockUseToastContainer = useToastContainer as jest.Mock

  beforeEach(() => {
    mockUseMutation.mockClear()
    mockUseRouter.mockClear()
    mockUseToastContainer.mockClear()
  })

  it('handles question creation and navigation', async () => {
    const mockNotify = jest.fn()
    const mockPush = jest.fn()
    const mockCreateQuestion = jest.fn().mockResolvedValue({
      data: { createQuestion: { id: '1' } },
      errors: []
    })

    mockUseMutation.mockReturnValue([mockCreateQuestion, { error: null }])
    mockUseRouter.mockReturnValue({ push: mockPush })
    mockUseToastContainer.mockReturnValue({ notify: mockNotify })

    const { result } = renderHook(() => useCreateQuestion())

    await act(async () => {
      await result.current.createQuestion({
        title: 'Test Title',
        description: 'Test Description'
      })
    })

    expect(mockCreateQuestion).toHaveBeenCalledWith({
      variables: {
        input: { title: 'Test Title', description: 'Test Description' }
      }
    })
    expect(mockPush).toHaveBeenCalledWith(Routes.Dashboard)
    expect(result.current.hasGlobalError).toBe(false)
  })

  it('handles global error notification', async () => {
    const mockNotify = jest.fn()
    const mockCreateQuestion = jest.fn().mockResolvedValue({
      data: null,
      errors: [{ message: 'Error' }]
    })

    mockUseMutation.mockReturnValue([
      mockCreateQuestion,
      { error: new Error('Error') }
    ])
    mockUseToastContainer.mockReturnValue({ notify: mockNotify })

    const { result } = renderHook(() => useCreateQuestion())

    await act(async () => {
      await result.current.createQuestion({
        title: 'Test Title',
        description: 'Test Description'
      })
    })

    expect(mockNotify).toHaveBeenCalledWith({
      type: 'error',
      message: 'Something went wrong. Please try again.'
    })
    expect(result.current.hasGlobalError).toBe(true)
  })
})
