import { type RenderResult, render, waitFor } from '@testing-library/react'
import { usePathname, useRouter } from 'next/navigation'

import { useAuthStore } from '../../services/use-auth-store/use-auth-store'
import { AuthChecker } from './auth-checker'

jest.mock('next/navigation', () => ({
  usePathname: jest.fn(),
  useRouter: jest.fn()
}))

jest.mock('../../services/use-auth-store/use-auth-store', () => ({
  useAuthStore: jest.fn()
}))

const usePathnameMock = usePathname as jest.Mock
const useAuthStoreMock = useAuthStore as jest.Mock
const useRouterMock = useRouter as jest.Mock

const renderComponent = (): RenderResult => render(<AuthChecker />)

describe('AuthChecker', () => {
  it('should redirect to login if user is not authenticated', async () => {
    const pushMock = jest.fn()

    usePathnameMock.mockReturnValue('/some-path')
    useAuthStoreMock.mockReturnValue(null)
    useRouterMock.mockReturnValue({ push: pushMock })

    renderComponent()

    await waitFor(() => {
      expect(pushMock).toHaveBeenCalledWith('/')
    })
  })

  it('should not redirect to login if user is authenticated', async () => {
    const pushMock = jest.fn()

    usePathnameMock.mockReturnValue('/some-path')
    useAuthStoreMock.mockReturnValue('some-user-id')
    useRouterMock.mockReturnValue({ push: pushMock })

    renderComponent()

    await waitFor(() => {
      expect(pushMock).not.toHaveBeenCalled()
    })
  })
})
