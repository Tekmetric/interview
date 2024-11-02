import {
  type RenderResult,
  fireEvent,
  render,
  screen
} from '@testing-library/react'

import { Login } from './login'
import { useLogin } from './services/use-login/use-login'

jest.mock('./services/use-login/use-login', () => ({
  useLogin: jest.fn()
}))

const useLoginMock = useLogin as jest.Mock

const renderComponent = (): RenderResult => render(<Login />)

describe('Login', () => {
  beforeEach(() => {
    useLoginMock.mockReturnValue({
      login: jest.fn()
    })
  })

  it('calls login handler', () => {
    const loginMock = jest.fn()

    useLoginMock.mockReturnValue({
      login: loginMock
    })

    renderComponent()

    expect(loginMock).not.toHaveBeenCalled()

    fireEvent.change(screen.getByTestId('email'), {
      target: { value: 'email@email.com' }
    })

    fireEvent.change(screen.getByTestId('password'), {
      target: { value: 'password' }
    })

    fireEvent.click(screen.getByTestId('submit-button'))

    expect(loginMock).toHaveBeenCalledWith(
      {
        email: 'email@email.com',
        password: 'password'
      },
      expect.anything(),
      expect.anything()
    )
  })

  it('shows validation errors', () => {
    const loginMock = jest.fn()
    useLoginMock.mockReturnValue({
      login: loginMock
    })

    renderComponent()

    expect(loginMock).not.toHaveBeenCalled()

    fireEvent.change(screen.getByTestId('email'), {
      target: { value: 'email' }
    })

    fireEvent.change(screen.getByTestId('password'), {
      target: { value: '' }
    })

    fireEvent.click(screen.getByTestId('submit-button'))

    expect(loginMock).not.toHaveBeenCalled()

    expect(screen.getByText('Invalid email address')).toBeInTheDocument()
    expect(screen.getByText('Required')).toBeInTheDocument()
  })
})
