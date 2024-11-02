import { useMutation } from '@tekmetric/graphql'
import { act, renderHook } from '@testing-library/react'

import { useToastContainer } from '../../../../../../services/use-toast-container/use-toast-container'
import { useCreateAnswer } from './use-create-answer'

jest.mock('@tekmetric/graphql', () => ({
  useMutation: jest.fn(),
  CreateAnswerDocument: {},
  GetQuestionDocument: {}
}))

jest.mock(
  '../../../../../../services/use-toast-container/use-toast-container',
  () => ({
    useToastContainer: jest.fn()
  })
)

describe('useCreateAnswer', () => {
  const useMutationMock = useMutation as jest.Mock
  const useToastContainerMock = useToastContainer as jest.Mock

  beforeEach(() => {
    useMutationMock.mockClear()
    useToastContainerMock.mockClear()
  })

  it('should handle answer creation and notify on success', async () => {
    const mockNotify = jest.fn()
    const mockCreateAnswer = jest.fn().mockResolvedValue({
      data: { createAnswer: { id: '1' } },
      errors: []
    })

    useMutationMock.mockReturnValue([mockCreateAnswer, { error: null }])
    useToastContainerMock.mockReturnValue({ notify: mockNotify })

    const { result } = renderHook(() => useCreateAnswer('questionId'))

    await act(async () => {
      await result.current.createAnswer({ description: 'Test Answer' })
    })

    expect(mockCreateAnswer).toHaveBeenCalledWith({
      variables: {
        input: { questionId: 'questionId', description: 'Test Answer' }
      }
    })
    expect(mockNotify).not.toHaveBeenCalledWith({
      type: 'error',
      message: 'Failed to create the answer'
    })
    expect(result.current.hasGlobalError).toBe(false)
  })

  it('should handle global error notification', async () => {
    const mockNotify = jest.fn()
    const mockCreateAnswer = jest.fn().mockResolvedValue({
      data: null,
      errors: [{ message: 'Error' }]
    })

    useMutationMock.mockReturnValue([
      mockCreateAnswer,
      { error: new Error('Error') }
    ])
    useToastContainerMock.mockReturnValue({ notify: mockNotify })

    const { result } = renderHook(() => useCreateAnswer('questionId'))

    await act(async () => {
      await result.current.createAnswer({ description: 'Test Answer' })
    })

    expect(mockNotify).toHaveBeenCalledWith({
      type: 'error',
      message: 'Failed to create the answer'
    })
    expect(result.current.hasGlobalError).toBe(true)
  })

  it('should notify on global error', () => {
    const mockNotify = jest.fn()

    useMutationMock.mockReturnValue([jest.fn(), { error: new Error('Error') }])
    useToastContainerMock.mockReturnValue({ notify: mockNotify })

    renderHook(() => useCreateAnswer('questionId'))

    expect(mockNotify).toHaveBeenCalledWith({
      type: 'error',
      message: 'Something went wrong. Please try again.'
    })
  })
})
