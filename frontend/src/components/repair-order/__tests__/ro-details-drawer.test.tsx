 
import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@/test/test-utils'
import { RODetailsDrawer } from '../ro-details-drawer'
import { useLocation } from 'wouter'

// Mock the useLocation hook
vi.mock('wouter', () => ({
  ...vi.importActual('wouter'),
  useLocation: vi.fn(),
  useSearch: () => 'roId=RO-123',
}));

// Mock the RODetailsContent component
vi.mock('../ro-details-form', () => ({
  RODetailsForm: vi.fn(() => <div>RODetailsForm</div>),
}));

describe('RODetailsDrawer', () => {
  it('should render the loading state initially', () => {
    (useLocation as vi.Mock).mockReturnValue(['/', vi.fn()]);
    render(<RODetailsDrawer />);
    const loadingComponent = screen.getByTestId('ro-details-loading');
    expect(loadingComponent).toBeInTheDocument();
  });
});
