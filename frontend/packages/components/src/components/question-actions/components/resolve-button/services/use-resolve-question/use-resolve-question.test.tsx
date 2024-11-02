import { useMutation } from '@tekmetric/graphql'
import { act, renderHook } from '@testing-library/react'

import { useToastContainer } from '../../../../../../services/use-toast-container/use-toast-container'
import { useResolveQuestion } from './use-resolve-question'

jest.mock('@tekmetric/graphql', () => ({
  useMutation: jest.fn(),
  ResolveQuestionDocument: {},
  GetQuestionsDocument: {},
  GetQuestionDocument: {}
}))

jest.mock(
  '../../../../../../services/use-toast-container/use-toast-container',
  () => ({
    useToastContainer: jest.fn()
  })
)

describe('useResolveQuestion', () => {
  const useMutationMock = useMutation as jest.Mock
  const useToastContainerMock = useToastContainer as jest.Mock

  beforeEach(() => {
    useMutationMock.mockClear()
    useToastContainerMock.mockClear()
  })

  it('should handle question resolution and notify on success', async () => {
    const mockNotify = jest.fn()
    const mockResolveQuestion = jest.fn().mockResolvedValue({
      data: { resolveQuestion: { id: '1' } },
      errors: []
    })

    useMutationMock.mockReturnValue([
      mockResolveQuestion,
      { error: null, loading: false }
    ])
    useToastContainerMock.mockReturnValue({ notify: mockNotify })

    const { result } = renderHook(() => useResolveQuestion('questionId'))

    await act(async () => {
      await result.current.resolveQuestion()
    })

    expect(mockResolveQuestion).toHaveBeenCalledWith({
      variables: { id: 'questionId' }
    })
    expect(mockNotify).toHaveBeenCalledWith({
      message: 'Question resolved successfully'
    })
    expect(result.current.hasGlobalError).toBe(false)
  })

  it('should handle global error notification', async () => {
    const mockNotify = jest.fn()
    const mockResolveQuestion = jest.fn().mockResolvedValue({
      data: null,
      errors: [{ message: 'Error' }]
    })

    useMutationMock.mockReturnValue([
      mockResolveQuestion,
      { error: new Error('Error'), loading: false }
    ])
    useToastContainerMock.mockReturnValue({ notify: mockNotify })

    const { result } = renderHook(() => useResolveQuestion('questionId'))

    await act(async () => {
      await result.current.resolveQuestion()
    })

    expect(mockNotify).toHaveBeenCalledWith({
      type: 'error',
      message: 'Failed to resolve question'
    })
    expect(result.current.hasGlobalError).toBe(true)
  })
})
