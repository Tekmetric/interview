import { fireEvent, render, screen } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { ThemeProvider } from '../../contexts/ThemeContext';
import { ThemeToggle } from './ThemeToggle';

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
};

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
});

// Mock matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(), // deprecated
    removeListener: vi.fn(), // deprecated
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});

// Wrapper component to provide theme context
const ThemeWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <ThemeProvider>{children}</ThemeProvider>
);

describe('ThemeToggle', () => {
  beforeEach(() => {
    localStorageMock.getItem.mockClear();
    localStorageMock.setItem.mockClear();
    vi.clearAllMocks();
  });

  it('renders all three theme options', () => {
    render(
      <ThemeWrapper>
        <ThemeToggle />
      </ThemeWrapper>
    );

    expect(screen.getByRole('button', { name: /system/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /light/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /dark/i })).toBeInTheDocument();
  });

  it('has proper accessibility attributes', () => {
    render(
      <ThemeWrapper>
        <ThemeToggle />
      </ThemeWrapper>
    );

    const group = screen.getByRole('group', { name: /theme selection/i });
    expect(group).toBeInTheDocument();

    const buttons = screen.getAllByRole('button');
    buttons.forEach(button => {
      expect(button).toHaveAttribute('aria-pressed');
    });
  });

  it('shows system as active by default', () => {
    render(
      <ThemeWrapper>
        <ThemeToggle />
      </ThemeWrapper>
    );

    const systemButton = screen.getByRole('button', { name: /system/i });
    expect(systemButton).toHaveAttribute('aria-pressed', 'true');
  });

  it('allows theme switching', () => {
    render(
      <ThemeWrapper>
        <ThemeToggle />
      </ThemeWrapper>
    );

    const lightButton = screen.getByRole('button', { name: /light/i });
    const darkButton = screen.getByRole('button', { name: /dark/i });

    // Click light theme
    fireEvent.click(lightButton);
    expect(lightButton).toHaveAttribute('aria-pressed', 'true');

    // Click dark theme
    fireEvent.click(darkButton);
    expect(darkButton).toHaveAttribute('aria-pressed', 'true');
  });

  it('applies correct CSS classes for active state', () => {
    render(
      <ThemeWrapper>
        <ThemeToggle />
      </ThemeWrapper>
    );

    const systemButton = screen.getByRole('button', { name: /system/i });

    // Should have active classes by default (system is default)
    expect(systemButton).toHaveClass('bg-blue-100');
    expect(systemButton).toHaveClass('text-blue-700');
  });

  it('renders correct icons for each theme', () => {
    render(
      <ThemeWrapper>
        <ThemeToggle />
      </ThemeWrapper>
    );

    // Each button should have an icon (SVG element)
    const buttons = screen.getAllByRole('button');
    buttons.forEach(button => {
      const icon = button.querySelector('svg');
      expect(icon).toBeInTheDocument();
    });
  });

  it('handles keyboard navigation', () => {
    render(
      <ThemeWrapper>
        <ThemeToggle />
      </ThemeWrapper>
    );

    const firstButton = screen.getByRole('button', { name: /system/i });
    firstButton.focus();

    expect(document.activeElement).toBe(firstButton);
  });
});
