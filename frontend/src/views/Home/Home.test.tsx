import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { MemoryRouter } from 'react-router-dom';
import Home from './Home';
import en from '../../utils/locales/en-US.json';

vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => en['translation'][key as keyof (typeof en)['translation']],
    // t: (key: string) => key // if we want to check by the translation key (not value)
  }),
}));

describe('Home', () => {
  it('should render the HelloMessage text', () => {
    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>,
    );

    expect(screen.getByText('Hello, tekmetric!')).toBeInTheDocument();
  });

  it('should render the View Tasks button', () => {
    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>,
    );

    // Check if the button is rendered with correct text and attributes
    const button = screen.getByTestId('view-tasks-link-button');
    expect(button).toBeInTheDocument();
    expect(button).toHaveAttribute('aria-label', 'View Tasks');
    expect(button).toHaveTextContent('View Tasks');
  });

  it('should render the EmojiEmotionsIcon', () => {
    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>,
    );

    const icon = screen.getByTestId('EmojiEmotionsIcon');
    expect(icon).toBeInTheDocument();
  });
});
