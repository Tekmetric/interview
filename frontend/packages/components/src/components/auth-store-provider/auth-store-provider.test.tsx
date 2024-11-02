import { render } from '@testing-library/react'

import { AuthStoreContext } from '../../contexts/auth-store-context'
import { createAuthStore, initAuthStore } from '../../stores/auth-store'
import { AuthStoreProvider } from './auth-store-provider'

jest.mock('../../stores/auth-store', () => ({
  createAuthStore: jest.fn(),
  initAuthStore: jest.fn()
}))

describe('AuthStoreProvider', () => {
  const mockCreateAuthStore = createAuthStore as jest.Mock
  const mockInitAuthStore = initAuthStore as jest.Mock

  beforeEach(() => {
    mockCreateAuthStore.mockClear()
    mockInitAuthStore.mockClear()
  })

  it('initializes the store and provide it via context', () => {
    const mockSession = 'mockSession'
    const mockStore = { some: 'store' }
    mockInitAuthStore.mockReturnValue(mockStore)
    mockCreateAuthStore.mockReturnValue(mockStore)

    const { getByText } = render(
      <AuthStoreProvider session={mockSession}>
        <AuthStoreContext.Consumer>
          {(value) => <span>{JSON.stringify(value)}</span>}
        </AuthStoreContext.Consumer>
      </AuthStoreProvider>
    )

    expect(mockInitAuthStore).toHaveBeenCalledWith(mockSession)
    expect(mockCreateAuthStore).toHaveBeenCalledWith(mockStore)
    expect(getByText(JSON.stringify(mockStore))).toBeInTheDocument()
  })
})
