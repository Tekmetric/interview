import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginForm from './login-form';

describe('LoginForm component', () => {
  const mockSetAuthData = jest.fn();

  beforeEach(() => {
    render(<LoginForm setAuthData={mockSetAuthData} />);
  });

  it('should render the "Sign In to Bhagavad Gita" text', () => {
    const signInText = screen.getByText('Sign in to Bhagavad Gita');
    expect(signInText).toBeInTheDocument();
  });

  it('should update formData when input fields are changed', async () => {
    const clientIdInput = screen.getByTestId('client-id-input');
    const clientSecretInput = screen.getByTestId('client-secret-input');

    userEvent.type(clientIdInput, 'test-client-id');
    userEvent.type(clientSecretInput, 'test-client-secret');

    await waitFor(() => {
      expect(clientIdInput).toHaveValue('test-client-id');
      expect(clientSecretInput).toHaveValue('test-client-secret');
    });
  });

  it('should call setAuthData when the form is submitted', () => {
    const submitButton = screen.getByText('Sign In');

    fireEvent.click(submitButton);

    expect(mockSetAuthData).toHaveBeenCalled();
  });

  it('should have a link to "https://bhagavadgita.io/api/"', () => {
    const linkElement = screen.getByText("Don't have an account?");

    expect(linkElement).toHaveAttribute('href', 'https://bhagavadgita.io/api/');
  });
});
