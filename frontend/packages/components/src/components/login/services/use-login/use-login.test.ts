import { useMutation } from '@tekmetric/graphql'
import { act, renderHook } from '@testing-library/react'
import { useRouter } from 'next/navigation'

import { Routes } from '../../../../enums/routes'
import { useAuthStore } from '../../../../services/use-auth-store/use-auth-store'
import { useLogin } from './use-login'

jest.mock('@tekmetric/graphql', () => ({
  useMutation: jest.fn(),
  LoginDocument: {}
}))

jest.mock('next/navigation', () => ({
  useRouter: jest.fn()
}))

jest.mock('../../../../services/use-auth-store/use-auth-store', () => ({
  useAuthStore: jest.fn()
}))

describe('useLogin', () => {
  const useMutationMock = useMutation as jest.Mock
  const useRouterMock = useRouter as jest.Mock
  const useAuthStoreMock = useAuthStore as jest.Mock

  beforeEach(() => {
    useMutationMock.mockClear()
    useRouterMock.mockClear()
    useAuthStoreMock.mockClear()
  })

  it('should handle login and navigation', async () => {
    const mockPush = jest.fn()
    const mockSetSession = jest.fn()
    const mockLogin = jest.fn().mockResolvedValue({
      data: { login: { userId: '1' } },
      errors: []
    })

    useMutationMock.mockReturnValue([mockLogin, { error: null }])
    useRouterMock.mockReturnValue({ push: mockPush })
    useAuthStoreMock.mockReturnValue({ setSession: mockSetSession })

    const { result } = renderHook(() => useLogin())

    await act(async () => {
      await result.current.login({
        email: 'test@example.com',
        password: 'password'
      })
    })

    expect(mockLogin).toHaveBeenCalledWith({
      variables: { email: 'test@example.com', password: 'password' }
    })
    expect(mockSetSession).toHaveBeenCalledWith({ userId: '1' })
    expect(mockPush).toHaveBeenCalledWith(Routes.Dashboard)
    expect(result.current.hasGlobalError).toBe(false)
  })

  it('should handle global error', async () => {
    const mockLogin = jest.fn().mockResolvedValue({
      data: null,
      errors: [{ message: 'Error' }]
    })

    useMutationMock.mockReturnValue([mockLogin, { error: new Error('Error') }])
    useAuthStoreMock.mockReturnValue({ setSession: jest.fn() })

    const { result } = renderHook(() => useLogin())

    await act(async () => {
      await result.current.login({
        email: 'test@example.com',
        password: 'password'
      })
    })

    expect(result.current.hasGlobalError).toBe(true)
  })
})
