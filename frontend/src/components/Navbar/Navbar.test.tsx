import { render, screen, fireEvent } from '@testing-library/react';
import Navbar from '@/components/Navbar';
import { vi, Mock } from 'vitest';
import useTranslationMenu from '@/hooks/useTranslationMenu';
import LanguageTypes from '@/types/Language';
import RouterUrl from '@/types/RouterUrl.ts';
import { MemoryRouter } from 'react-router-dom';
import '@testing-library/jest-dom';

vi.mock('@mui/material', async () => ({
  ...(await vi.importActual('@mui/material')),
  useMediaQuery: vi.fn(),
}));

// Mock useTranslationMenu hook
vi.mock('@/hooks/useTranslationMenu.ts', () => ({
  __esModule: true,
  default: vi.fn(() => ({
    translate: (key: string) => key,
    selectedLanguage: LanguageTypes.EN,
    menuEl: null,
    handleLanguageClose: vi.fn(),
    handleOnClickLanguage: vi.fn(),
    handleLanguageChange: vi.fn(),
  })),
}));

describe('Navbar Component', () => {
  it('should render Navbar with correct elements', () => {
    render(
      <MemoryRouter initialEntries={[RouterUrl.Home]}>
        <Navbar darkMode={false} setDarkMode={vi.fn()} />
      </MemoryRouter>,
    );

    expect(screen.getByText('app.title')).toBeInTheDocument();
    expect(screen.getByTestId('dark-mode-toggle-button')).toBeInTheDocument();
    expect(screen.getByTestId('language-button')).toBeInTheDocument();
  });

  it('should toggle dark mode on button click', () => {
    const setDarkMode = vi.fn();
    render(
      <MemoryRouter initialEntries={[RouterUrl.Home]}>
        <Navbar darkMode={false} setDarkMode={setDarkMode} />
      </MemoryRouter>,
    );

    fireEvent.click(screen.getByTestId('dark-mode-toggle-button'));
    expect(setDarkMode).toHaveBeenCalledWith(true);
  });

  it('should call handleLanguageChange with correct param ', () => {
    const handleOnClickLanguage = vi.fn();
    const handleLanguageClose = vi.fn();
    const handleLanguageChange = vi.fn();

    // Mock the hook with different values
    (useTranslationMenu as Mock).mockReturnValue({
      translate: (key: string) => key,
      selectedLanguage: LanguageTypes.EN,
      menuEl: document.createElement('div'),
      handleLanguageClose,
      handleOnClickLanguage,
      handleLanguageChange,
    });

    render(
      <MemoryRouter initialEntries={[RouterUrl.Home]}>
        <Navbar darkMode={false} setDarkMode={vi.fn()} />
      </MemoryRouter>,
    );

    fireEvent.click(screen.getByTestId('language-button'));
    expect(handleOnClickLanguage).toHaveBeenCalled();

    expect(screen.getByTestId('language-menu')).toBeInTheDocument();
    fireEvent.click(screen.getByTestId('language-en'));
    expect(handleLanguageChange).toHaveBeenCalledWith(LanguageTypes.EN);
  });
});
