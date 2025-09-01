import { act, renderHook } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';

import { useIsDesktop, useMediaQuery } from './useMediaQuery';

// Mock matchMedia
const mockMatchMedia = vi.fn();
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: mockMatchMedia,
});

describe('useMediaQuery', () => {
  let mockMediaQueryList: {
    matches: boolean;
    addEventListener: ReturnType<typeof vi.fn>;
    removeEventListener: ReturnType<typeof vi.fn>;
  };

  beforeEach(() => {
    vi.clearAllMocks();

    mockMediaQueryList = {
      matches: false,
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
    };

    mockMatchMedia.mockReturnValue(mockMediaQueryList);
  });

  describe('Basic Functionality', () => {
    it('returns initial match state', () => {
      mockMediaQueryList.matches = true;

      const { result } = renderHook(() => useMediaQuery('(min-width: 768px)'));

      expect(result.current).toBe(true);
    });

    it('returns false when media query does not match', () => {
      mockMediaQueryList.matches = false;

      const { result } = renderHook(() => useMediaQuery('(min-width: 768px)'));

      expect(result.current).toBe(false);
    });

    it('calls matchMedia with correct query', () => {
      const query = '(min-width: 1024px)';

      renderHook(() => useMediaQuery(query));

      expect(mockMatchMedia).toHaveBeenCalledWith(query);
    });

    it('sets up event listener on mount', () => {
      renderHook(() => useMediaQuery('(min-width: 768px)'));

      expect(mockMediaQueryList.addEventListener).toHaveBeenCalledWith(
        'change',
        expect.any(Function)
      );
    });

    it('removes event listener on unmount', () => {
      const { unmount } = renderHook(() => useMediaQuery('(min-width: 768px)'));

      unmount();

      expect(mockMediaQueryList.removeEventListener).toHaveBeenCalledWith(
        'change',
        expect.any(Function)
      );
    });
  });

  describe('Media Query Changes', () => {
    it('updates when media query match changes', () => {
      mockMediaQueryList.matches = false;

      const { result } = renderHook(() => useMediaQuery('(min-width: 768px)'));

      expect(result.current).toBe(false);

      // Simulate media query change
      mockMediaQueryList.matches = true;
      act(() => {
        const changeHandler = mockMediaQueryList.addEventListener.mock.calls[0][1];
        changeHandler({ matches: true });
      });

      expect(result.current).toBe(true);
    });

    it('updates when media query stops matching', () => {
      mockMediaQueryList.matches = true;

      const { result } = renderHook(() => useMediaQuery('(min-width: 768px)'));

      expect(result.current).toBe(true);

      // Simulate media query change
      act(() => {
        const changeHandler = mockMediaQueryList.addEventListener.mock.calls[0][1];
        changeHandler({ matches: false });
      });

      expect(result.current).toBe(false);
    });

    it('handles multiple rapid changes', () => {
      mockMediaQueryList.matches = false;

      const { result } = renderHook(() => useMediaQuery('(min-width: 768px)'));

      const changeHandler = mockMediaQueryList.addEventListener.mock.calls[0][1];

      act(() => {
        changeHandler({ matches: true });
        changeHandler({ matches: false });
        changeHandler({ matches: true });
      });

      expect(result.current).toBe(true);
    });
  });

  describe('Different Query Types', () => {
    it('works with min-width queries', () => {
      renderHook(() => useMediaQuery('(min-width: 1200px)'));

      expect(mockMatchMedia).toHaveBeenCalledWith('(min-width: 1200px)');
    });

    it('works with max-width queries', () => {
      renderHook(() => useMediaQuery('(max-width: 767px)'));

      expect(mockMatchMedia).toHaveBeenCalledWith('(max-width: 767px)');
    });

    it('works with orientation queries', () => {
      renderHook(() => useMediaQuery('(orientation: landscape)'));

      expect(mockMatchMedia).toHaveBeenCalledWith('(orientation: landscape)');
    });

    it('works with prefers-color-scheme queries', () => {
      renderHook(() => useMediaQuery('(prefers-color-scheme: dark)'));

      expect(mockMatchMedia).toHaveBeenCalledWith('(prefers-color-scheme: dark)');
    });

    it('works with complex queries', () => {
      const complexQuery = '(min-width: 768px) and (max-width: 1024px)';

      renderHook(() => useMediaQuery(complexQuery));

      expect(mockMatchMedia).toHaveBeenCalledWith(complexQuery);
    });
  });

  describe('Hook Dependencies', () => {
    it('re-runs effect when query changes', () => {
      const { rerender } = renderHook(({ query }) => useMediaQuery(query), {
        initialProps: { query: '(min-width: 768px)' },
      });

      expect(mockMatchMedia).toHaveBeenCalledTimes(1);
      expect(mockMatchMedia).toHaveBeenCalledWith('(min-width: 768px)');

      // Change the query
      rerender({ query: '(min-width: 1024px)' });

      expect(mockMatchMedia).toHaveBeenCalledTimes(2);
      expect(mockMatchMedia).toHaveBeenCalledWith('(min-width: 1024px)');
    });

    it('cleans up previous listener when query changes', () => {
      const { rerender } = renderHook(({ query }) => useMediaQuery(query), {
        initialProps: { query: '(min-width: 768px)' },
      });

      expect(mockMediaQueryList.addEventListener).toHaveBeenCalledTimes(1);

      // Change the query
      rerender({ query: '(min-width: 1024px)' });

      expect(mockMediaQueryList.removeEventListener).toHaveBeenCalledTimes(1);
      expect(mockMediaQueryList.addEventListener).toHaveBeenCalledTimes(2);
    });
  });

  describe('Error Handling', () => {
    it('handles matchMedia throwing an error', () => {
      mockMatchMedia.mockImplementation(() => {
        throw new Error('matchMedia not supported');
      });

      // Current implementation doesn't handle this gracefully, so it will throw
      expect(() => {
        renderHook(() => useMediaQuery('(min-width: 768px)'));
      }).toThrow('matchMedia not supported');
    });

    it('handles addEventListener throwing an error', () => {
      mockMediaQueryList.addEventListener.mockImplementation(() => {
        throw new Error('addEventListener failed');
      });

      // Current implementation doesn't handle this gracefully, so it will throw
      expect(() => {
        renderHook(() => useMediaQuery('(min-width: 768px)'));
      }).toThrow('addEventListener failed');
    });
  });
});

describe('useIsDesktop', () => {
  let mockMediaQueryList: {
    matches: boolean;
    addEventListener: ReturnType<typeof vi.fn>;
    removeEventListener: ReturnType<typeof vi.fn>;
  };

  beforeEach(() => {
    vi.clearAllMocks();

    mockMediaQueryList = {
      matches: false,
      addEventListener: vi.fn(),
      removeEventListener: vi.fn(),
    };

    mockMatchMedia.mockReturnValue(mockMediaQueryList);
  });

  describe('Desktop Detection', () => {
    it('returns true for desktop screen sizes', () => {
      mockMediaQueryList.matches = true;

      const { result } = renderHook(() => useIsDesktop());

      expect(result.current).toBe(true);
      expect(mockMatchMedia).toHaveBeenCalledWith('(min-width: 1024px)');
    });

    it('returns false for mobile/tablet screen sizes', () => {
      mockMediaQueryList.matches = false;

      const { result } = renderHook(() => useIsDesktop());

      expect(result.current).toBe(false);
      expect(mockMatchMedia).toHaveBeenCalledWith('(min-width: 1024px)');
    });

    it('updates when screen size changes', () => {
      mockMediaQueryList.matches = false;

      const { result } = renderHook(() => useIsDesktop());

      expect(result.current).toBe(false);

      // Simulate screen size change to desktop
      act(() => {
        const changeHandler = mockMediaQueryList.addEventListener.mock.calls[0][1];
        changeHandler({ matches: true });
      });

      expect(result.current).toBe(true);
    });

    it('uses Tailwind lg breakpoint (1024px)', () => {
      renderHook(() => useIsDesktop());

      expect(mockMatchMedia).toHaveBeenCalledWith('(min-width: 1024px)');
    });
  });

  describe('Responsive Behavior', () => {
    it('responds to window resize events', () => {
      mockMediaQueryList.matches = true;

      const { result } = renderHook(() => useIsDesktop());

      expect(result.current).toBe(true);

      // Simulate resize to mobile
      act(() => {
        const changeHandler = mockMediaQueryList.addEventListener.mock.calls[0][1];
        changeHandler({ matches: false });
      });

      expect(result.current).toBe(false);

      // Simulate resize back to desktop
      act(() => {
        const changeHandler = mockMediaQueryList.addEventListener.mock.calls[0][1];
        changeHandler({ matches: true });
      });

      expect(result.current).toBe(true);
    });

    it('properly cleans up listeners', () => {
      const { unmount } = renderHook(() => useIsDesktop());

      expect(mockMediaQueryList.addEventListener).toHaveBeenCalledWith(
        'change',
        expect.any(Function)
      );

      unmount();

      expect(mockMediaQueryList.removeEventListener).toHaveBeenCalledWith(
        'change',
        expect.any(Function)
      );
    });
  });

  describe('Integration with useMediaQuery', () => {
    it('is essentially useMediaQuery with predefined query', () => {
      const desktopResult = renderHook(() => useIsDesktop());
      const mediaQueryResult = renderHook(() => useMediaQuery('(min-width: 1024px)'));

      expect(desktopResult.result.current).toBe(mediaQueryResult.result.current);
    });
  });
});
