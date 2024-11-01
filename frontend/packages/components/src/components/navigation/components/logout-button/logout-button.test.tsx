import {
  type RenderResult,
  fireEvent,
  render,
  screen
} from '@testing-library/react'
import { useRouter } from 'next/navigation'

import { useAuthStore } from '../../../../services/use-auth-store/use-auth-store'
import { LogoutButton } from './logout-button'

jest.mock('next/navigation', () => ({
  useRouter: jest.fn()
}))

jest.mock('../../../../services/use-auth-store/use-auth-store', () => ({
  useAuthStore: jest.fn()
}))

jest.mock('@tekmetric/ui/icon', () => ({
  Icon: () => <span />
}))

const renderComponent = (): RenderResult => render(<LogoutButton />)

describe('LogoutButton', () => {
  const useRouterMock = useRouter as jest.Mock
  const useAuthStoreMock = useAuthStore as jest.Mock

  beforeEach(() => {
    useRouterMock.mockClear()
    useAuthStoreMock.mockClear()
  })

  it('should handle logout and navigation', () => {
    const mockPush = jest.fn()
    const mockClearSession = jest.fn()

    useRouterMock.mockReturnValue({ push: mockPush })
    useAuthStoreMock.mockReturnValue({ clearSession: mockClearSession })

    renderComponent()

    fireEvent.click(screen.getByText('Logout'))

    expect(mockClearSession).toHaveBeenCalled()
    expect(mockPush).toHaveBeenCalledWith('/')
  })
})
