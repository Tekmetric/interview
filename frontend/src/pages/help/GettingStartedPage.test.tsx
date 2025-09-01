import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { ThemeProvider } from '../../contexts/ThemeContext';
import { GettingStartedPage } from './GettingStartedPage';

// Test wrapper component
const TestWrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
  <BrowserRouter>
    <ThemeProvider>{children}</ThemeProvider>
  </BrowserRouter>
);

describe('GettingStartedPage', () => {
  it('renders the main heading', () => {
    render(
      <TestWrapper>
        <GettingStartedPage />
      </TestWrapper>
    );

    expect(screen.getByText('Getting Started')).toBeInTheDocument();
  });

  it('displays welcome message', () => {
    render(
      <TestWrapper>
        <GettingStartedPage />
      </TestWrapper>
    );

    expect(
      screen.getByText('Welcome to the User Management Dashboard! Follow these simple steps to get started.')
    ).toBeInTheDocument();
  });

  it('shows dashboard overview section', () => {
    render(
      <TestWrapper>
        <GettingStartedPage />
      </TestWrapper>
    );

    expect(screen.getByText('Dashboard Overview')).toBeInTheDocument();
    expect(screen.getByText('The dashboard has three main sections:')).toBeInTheDocument();
  });

  it('lists dashboard sections', () => {
    render(
      <TestWrapper>
        <GettingStartedPage />
      </TestWrapper>
    );

    expect(screen.getByText(/Home:/)).toBeInTheDocument();
    expect(screen.getByText('Overview and quick access to features')).toBeInTheDocument();
    expect(screen.getByText(/Users:/)).toBeInTheDocument();
    expect(screen.getByText('Manage users with search, add, edit, and delete functionality')).toBeInTheDocument();
    expect(screen.getByText(/Help:/)).toBeInTheDocument();
    expect(screen.getByText('Documentation and guides')).toBeInTheDocument();
  });

  it('displays quick start steps section', () => {
    render(
      <TestWrapper>
        <GettingStartedPage />
      </TestWrapper>
    );

    expect(screen.getByText('Quick Start Steps')).toBeInTheDocument();
  });

  it('shows numbered steps with descriptions', () => {
    render(
      <TestWrapper>
        <GettingStartedPage />
      </TestWrapper>
    );

    // Step 1
    expect(screen.getByText('1')).toBeInTheDocument();
    expect(screen.getByText('Visit the Users Page')).toBeInTheDocument();
    expect(screen.getByText('Navigate to the Users section to see the user list and available actions.')).toBeInTheDocument();

    // Step 2
    expect(screen.getByText('2')).toBeInTheDocument();
    expect(screen.getByText('Try the Search')).toBeInTheDocument();
    expect(screen.getByText('Use the search bar to find users by name, email, or company.')).toBeInTheDocument();

    // Step 3
    expect(screen.getByText('3')).toBeInTheDocument();
    expect(screen.getByText('View User Details')).toBeInTheDocument();
    expect(screen.getByText('Click on any user name to see their detailed information.')).toBeInTheDocument();
  });

  it('displays navigation tips section', () => {
    render(
      <TestWrapper>
        <GettingStartedPage />
      </TestWrapper>
    );

    expect(screen.getByText('Navigation Tips')).toBeInTheDocument();
    expect(screen.getByText('Use the navigation bar to switch between sections')).toBeInTheDocument();
    expect(screen.getByText('Toggle between light and dark themes using the theme button')).toBeInTheDocument();
    expect(screen.getByText('Pagination controls help navigate through large user lists')).toBeInTheDocument();
    expect(screen.getByText('Sort columns by clicking on table headers')).toBeInTheDocument();
  });

  it('shows the ready to explore section', () => {
    render(
      <TestWrapper>
        <GettingStartedPage />
      </TestWrapper>
    );

    expect(screen.getByText(/Ready to explore\?/)).toBeInTheDocument();
    expect(screen.getByText('Head to the Users section to start managing users!')).toBeInTheDocument();
  });

  it('has proper heading hierarchy', () => {
    render(
      <TestWrapper>
        <GettingStartedPage />
      </TestWrapper>
    );

    // Main heading should be h2
    const mainHeading = screen.getByRole('heading', { level: 2 });
    expect(mainHeading).toHaveTextContent('Getting Started');

    // Section headings should be h3
    const sectionHeadings = screen.getAllByRole('heading', { level: 3 });
    expect(sectionHeadings).toHaveLength(3);
    expect(sectionHeadings[0]).toHaveTextContent('Dashboard Overview');
    expect(sectionHeadings[1]).toHaveTextContent('Quick Start Steps');
    expect(sectionHeadings[2]).toHaveTextContent('Navigation Tips');

    // Step headings should be h4
    const stepHeadings = screen.getAllByRole('heading', { level: 4 });
    expect(stepHeadings).toHaveLength(3);
    expect(stepHeadings[0]).toHaveTextContent('Visit the Users Page');
    expect(stepHeadings[1]).toHaveTextContent('Try the Search');
    expect(stepHeadings[2]).toHaveTextContent('View User Details');
  });
});
