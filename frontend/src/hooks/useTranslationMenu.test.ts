import { MouseEvent } from 'react';
import { renderHook, act } from '@testing-library/react-hooks';
import { useTranslation } from 'react-i18next';
import useTranslationMenu from '@/hooks/useTranslationMenu';
import { vi, Mock } from 'vitest';
import LanguageTypes from '@/types/Language.ts';

// Mocking the useTranslation hook from react-i18next
vi.mock('react-i18next', () => ({
  useTranslation: vi.fn(),
}));

describe('useTranslationMenu hook', () => {
  const mockChangeLanguage = vi.fn().mockResolvedValue(null);

  beforeEach(() => {
    (useTranslation as Mock).mockReturnValue({
      i18n: {
        changeLanguage: mockChangeLanguage,
        language: 'en',
      },
      t: (key: string) => key,
    });
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it('should initialize with correct default values', () => {
    const { result } = renderHook(() => useTranslationMenu());

    expect(result.current.selectedLanguage).toBe('en');
    expect(result.current.menuEl).toBeNull();
  });

  it('should handle menu opening correctly', () => {
    const { result } = renderHook(() => useTranslationMenu());
    const mockEvent = {
      currentTarget: document.createElement('button'),
    } as unknown as MouseEvent<HTMLElement>;

    act(() => {
      result.current.handleOnClickLanguage(mockEvent);
    });

    expect(result.current.menuEl).toBe(mockEvent.currentTarget);
  });

  it('should close the menu correctly', () => {
    const { result } = renderHook(() => useTranslationMenu());
    const mockEvent = {
      currentTarget: document.createElement('button'),
    } as unknown as MouseEvent<HTMLElement>;

    act(() => {
      result.current.handleOnClickLanguage(mockEvent);
    });

    act(() => {
      result.current.handleLanguageClose();
    });

    expect(result.current.menuEl).toBeNull();
  });

  it('should change language and close the menu', async () => {
    const { result } = renderHook(() => useTranslationMenu());

    act(() => {
      result.current.handleLanguageChange(LanguageTypes.EN);
    });

    expect(mockChangeLanguage).toHaveBeenCalledWith(LanguageTypes.EN);

    await act(async () => {
      await mockChangeLanguage(LanguageTypes.EN);
    });

    expect(result.current.menuEl).toBeNull();
  });
});
