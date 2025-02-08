import { render, waitFor } from '@testing-library/react'
import RegisterServiceWorker from '../RegisterServiceWorker'

describe('RegisterServiceWorker', () => {
  let originalServiceWorker: any

  beforeEach(() => {
    originalServiceWorker = navigator.serviceWorker
    navigator.serviceWorker = {
      register: jest.fn().mockResolvedValue({ scope: 'test-scope' }),
    }
  })

  afterEach(() => {
    navigator.serviceWorker = originalServiceWorker
    jest.clearAllMocks()
  })

  it('registers the service worker on window load', async () => {
    render(<RegisterServiceWorker />)
    window.dispatchEvent(new Event('load'))
    expect(navigator.serviceWorker.register).toHaveBeenCalledWith(
      '/service-worker.js'
    )
  })

  it('handles registration failure', async () => {
    const registrationError = new Error('Registration failed')
    navigator.serviceWorker.register = jest
      .fn()
      .mockRejectedValue(registrationError)
    const consoleErrorSpy = jest
      .spyOn(console, 'error')
      .mockImplementation(() => {})
    render(<RegisterServiceWorker />)
    window.dispatchEvent(new Event('load'))

    await waitFor(() =>
      expect(consoleErrorSpy).toHaveBeenCalledWith(
        'Service Worker registration failed:',
        registrationError
      )
    )
    consoleErrorSpy.mockRestore()
  })
})
