import React from 'react';
import { render, act } from '@testing-library/react';
import { ThemeProvider, useTheme } from './ThemeContext';

describe('ThemeContext', () => {
  beforeEach(() => {
    localStorage.clear();
    document.documentElement.classList.remove('dark');
  });

  describe('useTheme', () => {
    test('throws error when used outside ThemeProvider', () => {
      const TestComponent = () => {
        useTheme();
        return <div>Test</div>;
      };

      // Suppress error boundary console errors
      const consoleError = jest.spyOn(console, 'error').mockImplementation(() => {});

      expect(() => {
        render(<TestComponent />);
      }).toThrow('useTheme must be used within a ThemeProvider');

      consoleError.mockRestore();
    });

    test('returns theme context when used inside ThemeProvider', () => {
      let context;
      const TestComponent = () => {
        context = useTheme();
        return <div>Test</div>;
      };

      render(
        <ThemeProvider>
          <TestComponent />
        </ThemeProvider>
      );

      expect(context).toBeDefined();
      expect(context.theme).toBeDefined();
      expect(context.toggleTheme).toBeDefined();
      expect(context.isDark).toBeDefined();
    });
  });

  describe('ThemeProvider', () => {
    test('initializes with light theme by default', () => {
      let context;
      const TestComponent = () => {
        context = useTheme();
        return <div>Test</div>;
      };

      render(
        <ThemeProvider>
          <TestComponent />
        </ThemeProvider>
      );

      expect(context.theme).toBe('light');
      expect(context.isDark).toBe(false);
    });

    test('initializes with saved theme from localStorage', () => {
      localStorage.setItem('theme', 'dark');

      let context;
      const TestComponent = () => {
        context = useTheme();
        return <div>Test</div>;
      };

      render(
        <ThemeProvider>
          <TestComponent />
        </ThemeProvider>
      );

      expect(context.theme).toBe('dark');
      expect(context.isDark).toBe(true);
    });

    test('initializes with system preference when no saved theme', () => {
      // Mock matchMedia to return dark mode preference
      Object.defineProperty(window, 'matchMedia', {
        writable: true,
        value: jest.fn().mockImplementation(query => ({
          matches: query === '(prefers-color-scheme: dark)',
          media: query,
          onchange: null,
          addListener: jest.fn(),
          removeListener: jest.fn(),
          addEventListener: jest.fn(),
          removeEventListener: jest.fn(),
          dispatchEvent: jest.fn(),
        })),
      });

      let context;
      const TestComponent = () => {
        context = useTheme();
        return <div>Test</div>;
      };

      render(
        <ThemeProvider>
          <TestComponent />
        </ThemeProvider>
      );

      expect(context.theme).toBe('dark');
    });

    test('toggles theme from light to dark', () => {
      let context;
      const TestComponent = () => {
        context = useTheme();
        return <div>Test</div>;
      };

      render(
        <ThemeProvider>
          <TestComponent />
        </ThemeProvider>
      );

      expect(context.theme).toBe('light');

      act(() => {
        context.toggleTheme();
      });

      expect(context.theme).toBe('dark');
      expect(context.isDark).toBe(true);
    });

    test('toggles theme from dark to light', () => {
      localStorage.setItem('theme', 'dark');

      let context;
      const TestComponent = () => {
        context = useTheme();
        return <div>Test</div>;
      };

      render(
        <ThemeProvider>
          <TestComponent />
        </ThemeProvider>
      );

      expect(context.theme).toBe('dark');

      act(() => {
        context.toggleTheme();
      });

      expect(context.theme).toBe('light');
      expect(context.isDark).toBe(false);
    });

    test('saves theme to localStorage when changed', () => {
      let context;
      const TestComponent = () => {
        context = useTheme();
        return <div>Test</div>;
      };

      render(
        <ThemeProvider>
          <TestComponent />
        </ThemeProvider>
      );

      act(() => {
        context.toggleTheme();
      });

      expect(localStorage.getItem('theme')).toBe('dark');
    });

    test('adds dark class to document when theme is dark', () => {
      localStorage.setItem('theme', 'dark');

      const TestComponent = () => {
        useTheme();
        return <div>Test</div>;
      };

      render(
        <ThemeProvider>
          <TestComponent />
        </ThemeProvider>
      );

      expect(document.documentElement.classList.contains('dark')).toBe(true);
    });

    test('removes dark class from document when theme is light', () => {
      document.documentElement.classList.add('dark');

      const TestComponent = () => {
        useTheme();
        return <div>Test</div>;
      };

      render(
        <ThemeProvider>
          <TestComponent />
        </ThemeProvider>
      );

      expect(document.documentElement.classList.contains('dark')).toBe(false);
    });

    test('updates document class when theme toggles', () => {
      let context;
      const TestComponent = () => {
        context = useTheme();
        return <div>Test</div>;
      };

      render(
        <ThemeProvider>
          <TestComponent />
        </ThemeProvider>
      );

      expect(document.documentElement.classList.contains('dark')).toBe(false);

      act(() => {
        context.toggleTheme();
      });

      expect(document.documentElement.classList.contains('dark')).toBe(true);

      act(() => {
        context.toggleTheme();
      });

      expect(document.documentElement.classList.contains('dark')).toBe(false);
    });
  });
});
