import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, expect, it, vi } from 'vitest';

import { ThemeProvider } from '../../contexts/ThemeContext';
import { FeaturesPage } from './FeaturesPage';

// Mock the theme context
const mockThemeContext = {
  theme: 'system' as const,
  setTheme: vi.fn(),
  actualTheme: 'light' as const,
};

vi.mock('../../contexts/ThemeContext', () => ({
  ThemeProvider: ({ children }: { children: React.ReactNode }) => children,
  useTheme: () => mockThemeContext,
}));

// Test wrapper to provide routing context
const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <BrowserRouter>
    <ThemeProvider>{children}</ThemeProvider>
  </BrowserRouter>
);

describe('FeaturesPage', () => {
  it('renders main heading', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText('Technical Features')).toBeInTheDocument();
  });

  it('displays description text', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(
      screen.getByText(/this production-ready react application demonstrates/i)
    ).toBeInTheDocument();
  });

  it('renders core technologies section', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText('Core Technologies')).toBeInTheDocument();
    expect(screen.getByText('React & Router')).toBeInTheDocument();
    expect(screen.getByText('TypeScript')).toBeInTheDocument();
    expect(screen.getByText('Styling & UI')).toBeInTheDocument();
    expect(screen.getByText('State Management')).toBeInTheDocument();
  });

  it('displays React & Router features', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText(/react 18 with functional components/i)).toBeInTheDocument();
    expect(screen.getByText(/react router v7 with nested routing/i)).toBeInTheDocument();
    expect(screen.getByText(/component-based architecture/i)).toBeInTheDocument();
  });

  it('displays TypeScript features', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText(/typescript 5.0\+ with strict mode/i)).toBeInTheDocument();
    expect(screen.getByText(/full type safety throughout/i)).toBeInTheDocument();
    expect(screen.getByText(/enhanced intellisense support/i)).toBeInTheDocument();
  });

  it('renders key features section', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText('Key Features')).toBeInTheDocument();
    expect(screen.getByText('User Management')).toBeInTheDocument();
    expect(screen.getByText('Form Validation & Data Handling')).toBeInTheDocument();
    expect(screen.getByText('Theme System')).toBeInTheDocument();
    expect(screen.getByText('Accessibility & UX')).toBeInTheDocument();
  });

  it('displays user management features', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText(/crud operations with server state caching/i)).toBeInTheDocument();
    expect(screen.getByText(/advanced search across multiple fields/i)).toBeInTheDocument();
    expect(screen.getByText(/pagination with configurable page sizes/i)).toBeInTheDocument();
  });

  it('displays form validation features', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(
      screen.getByText(/zod schema-based validation with typescript inference/i)
    ).toBeInTheDocument();
    expect(screen.getByText(/react query for server state management/i)).toBeInTheDocument();
  });

  it('displays theme system features', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText(/three-mode theming: light, dark, system/i)).toBeInTheDocument();
    expect(screen.getByText(/automatic system preference detection/i)).toBeInTheDocument();
    expect(screen.getByText(/context-based theme management/i)).toBeInTheDocument();
  });

  it('renders development stack section', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText('Development Stack')).toBeInTheDocument();
    expect(screen.getByText('Frontend')).toBeInTheDocument();
    expect(screen.getByText('Styling')).toBeInTheDocument();
    expect(screen.getByText('Data & Forms')).toBeInTheDocument();
    expect(screen.getByText('Quality')).toBeInTheDocument();
  });

  it('displays development stack technologies', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText('• React 18 with functional components')).toBeInTheDocument();
    expect(screen.getByText('• TypeScript 5.0+ with strict mode')).toBeInTheDocument();
    expect(screen.getByText('• Tailwind CSS 3.4 utility-first')).toBeInTheDocument();
    expect(screen.getByText('• React Query for server state')).toBeInTheDocument();
    expect(screen.getByText(/• ESLint \+ Prettier/)).toBeInTheDocument();
  });

  it('renders testing & quality section', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText('Testing & Quality')).toBeInTheDocument();
    expect(screen.getByText(/comprehensive unit tests with vitest/i)).toBeInTheDocument();
    expect(screen.getByText(/react testing library integration/i)).toBeInTheDocument();
  });

  it('renders developer experience section', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText('Developer Experience')).toBeInTheDocument();
    expect(screen.getByText(/hot module replacement with vite/i)).toBeInTheDocument();
    expect(screen.getByText(/react query devtools integration/i)).toBeInTheDocument();
  });

  it('renders production-ready architecture section', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText('Production-Ready Architecture')).toBeInTheDocument();
    expect(
      screen.getByText(/enterprise-level react development with comprehensive testing/i)
    ).toBeInTheDocument();
  });

  it('displays accessibility features', () => {
    render(
      <TestWrapper>
        <FeaturesPage />
      </TestWrapper>
    );

    expect(screen.getByText(/comprehensive aria labels and semantic html/i)).toBeInTheDocument();
    expect(screen.getByText(/keyboard navigation support/i)).toBeInTheDocument();
    expect(screen.getByText(/screen reader optimizations/i)).toBeInTheDocument();
  });
});
