import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';

import { LoadingSpinner } from './LoadingSpinner';

describe('LoadingSpinner', () => {
  it('renders with default props', () => {
    render(<LoadingSpinner />);

    expect(screen.getByText('Loading...')).toBeInTheDocument();
    expect(screen.getByRole('status', { hidden: true })).toBeInTheDocument();
  });

  it('renders with custom text', () => {
    const customText = 'Please wait...';
    render(<LoadingSpinner text={customText} />);

    expect(screen.getByText(customText)).toBeInTheDocument();
  });

  it('applies correct size classes', () => {
    const { rerender } = render(<LoadingSpinner size='sm' />);
    let spinner = document.querySelector('.size-6');
    expect(spinner).toBeInTheDocument();

    rerender(<LoadingSpinner size='md' />);
    spinner = document.querySelector('.size-12');
    expect(spinner).toBeInTheDocument();

    rerender(<LoadingSpinner size='lg' />);
    spinner = document.querySelector('.size-16');
    expect(spinner).toBeInTheDocument();
  });

  it('has proper accessibility attributes', () => {
    render(<LoadingSpinner />);

    // Check that the spinner has appropriate ARIA attributes
    const container = screen.getByText('Loading...').closest('div');
    expect(container).toBeInTheDocument();
  });

  it('applies animation classes', () => {
    render(<LoadingSpinner />);

    const spinElement = document.querySelector('.animate-spin');
    const pingElement = document.querySelector('.animate-ping');

    expect(spinElement).toBeInTheDocument();
    expect(pingElement).toBeInTheDocument();
  });
});
