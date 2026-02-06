import { act, renderHook } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { useToast } from './useToast';

// Mock timers for duration testing
vi.useFakeTimers();

describe('useToast', () => {
  beforeEach(() => {
    vi.clearAllTimers();
  });

  afterEach(() => {
    vi.runOnlyPendingTimers();
    vi.useRealTimers();
    vi.useFakeTimers();
  });

  describe('Initial State', () => {
    it('starts with empty toasts array', () => {
      const { result } = renderHook(() => useToast());

      expect(result.current.toasts).toEqual([]);
    });

    it('provides all expected methods', () => {
      const { result } = renderHook(() => useToast());

      expect(typeof result.current.addSuccessToast).toBe('function');
      expect(typeof result.current.addErrorToast).toBe('function');
      expect(typeof result.current.removeToast).toBe('function');
    });
  });

  describe('Adding Toasts', () => {
    it('adds success toast with default duration', () => {
      const { result } = renderHook(() => useToast());

      act(() => {
        result.current.addSuccessToast('Success Title', 'Success message');
      });

      expect(result.current.toasts).toHaveLength(1);
      expect(result.current.toasts[0]).toMatchObject({
        type: 'success',
        title: 'Success Title',
        message: 'Success message',
        duration: 5000,
      });
      expect(result.current.toasts[0].id).toMatch(/^toast-\d+-[a-z0-9]+$/);
    });

    it('adds error toast with custom duration', () => {
      const { result } = renderHook(() => useToast());

      act(() => {
        result.current.addErrorToast('Error Title', 'Error message', 10000);
      });

      expect(result.current.toasts).toHaveLength(1);
      expect(result.current.toasts[0]).toMatchObject({
        type: 'error',
        title: 'Error Title',
        message: 'Error message',
        duration: 10000,
      });
    });

    it('adds multiple toasts', () => {
      const { result } = renderHook(() => useToast());

      act(() => {
        result.current.addSuccessToast('First');
        result.current.addErrorToast('Second');
      });

      expect(result.current.toasts).toHaveLength(2);
      expect(result.current.toasts[0].title).toBe('First');
      expect(result.current.toasts[1].title).toBe('Second');
    });

    it('generates unique IDs for each toast', () => {
      const { result } = renderHook(() => useToast());

      act(() => {
        result.current.addSuccessToast('First');
        result.current.addSuccessToast('Second');
      });

      expect(result.current.toasts).toHaveLength(2);
      expect(result.current.toasts[0].id).not.toBe(result.current.toasts[1].id);
    });

    it('returns toast ID when adding toast', () => {
      const { result } = renderHook(() => useToast());

      let toastId: string | undefined;
      act(() => {
        toastId = result.current.addSuccessToast('Test');
      });

      expect(toastId).toBeDefined();
      expect(toastId).toBe(result.current.toasts[0].id);
    });
  });

  describe('Removing Toasts', () => {
    it('removes toast by ID', () => {
      const { result } = renderHook(() => useToast());

      let toastId: string;
      act(() => {
        toastId = result.current.addSuccessToast('Test Toast');
      });

      expect(result.current.toasts).toHaveLength(1);

      act(() => {
        result.current.removeToast(toastId);
      });

      expect(result.current.toasts).toHaveLength(0);
    });

    it('only removes the specified toast', () => {
      const { result } = renderHook(() => useToast());

      let firstId: string | undefined;
      let secondId: string | undefined;

      act(() => {
        firstId = result.current.addSuccessToast('First');
        secondId = result.current.addErrorToast('Second');
      });

      expect(result.current.toasts).toHaveLength(2);

      act(() => {
        if (firstId) {
          result.current.removeToast(firstId);
        }
      });

      expect(result.current.toasts).toHaveLength(1);
      expect(result.current.toasts[0].id).toBe(secondId);
    });

    it('handles removing non-existent toast gracefully', () => {
      const { result } = renderHook(() => useToast());

      act(() => {
        result.current.addSuccessToast('Test');
      });

      expect(result.current.toasts).toHaveLength(1);

      act(() => {
        result.current.removeToast('non-existent-id');
      });

      expect(result.current.toasts).toHaveLength(1);
    });
  });

  describe('Default Duration Options', () => {
    it('uses hook-level default duration', () => {
      const { result } = renderHook(() => useToast({ duration: 3000 }));

      act(() => {
        result.current.addSuccessToast('Test');
      });

      expect(result.current.toasts[0].duration).toBe(3000);
    });

    it('method duration overrides hook default', () => {
      const { result } = renderHook(() => useToast({ duration: 3000 }));

      act(() => {
        result.current.addSuccessToast('Test', 'Message', 7000);
      });

      expect(result.current.toasts[0].duration).toBe(7000);
    });

    it('falls back to global default when no options provided', () => {
      const { result } = renderHook(() => useToast());

      act(() => {
        result.current.addSuccessToast('Test');
      });

      expect(result.current.toasts[0].duration).toBe(5000);
    });
  });

  describe('Toast Properties', () => {
    it('includes onClose callback in toast', () => {
      const { result } = renderHook(() => useToast());

      act(() => {
        result.current.addSuccessToast('Test');
      });

      const toast = result.current.toasts[0];
      expect(typeof toast.onClose).toBe('function');

      // Test that onClose works
      act(() => {
        toast.onClose(toast.id);
      });

      expect(result.current.toasts).toHaveLength(0);
    });

    it('creates toast with all required properties', () => {
      const { result } = renderHook(() => useToast());

      act(() => {
        result.current.addSuccessToast('Title', 'Message', 8000);
      });

      const toast = result.current.toasts[0];
      expect(toast).toEqual({
        id: expect.stringMatching(/^toast-\d+-[a-z0-9]+$/),
        type: 'success',
        title: 'Title',
        message: 'Message',
        duration: 8000,
        onClose: expect.any(Function),
      });
    });

    it('handles undefined message correctly', () => {
      const { result } = renderHook(() => useToast());

      act(() => {
        result.current.addSuccessToast('Title');
      });

      const toast = result.current.toasts[0];
      expect(toast.message).toBeUndefined();
    });
  });

  describe('ID Generation', () => {
    it('generates IDs with correct format', () => {
      const { result } = renderHook(() => useToast());

      act(() => {
        result.current.addSuccessToast('Test');
      });

      const id = result.current.toasts[0].id;
      expect(id).toMatch(/^toast-\d+-[a-z0-9]+$/);
    });

    it('generates different IDs for simultaneous toasts', () => {
      const { result } = renderHook(() => useToast());

      const ids: string[] = [];

      act(() => {
        ids.push(result.current.addSuccessToast('Test 1'));
        ids.push(result.current.addSuccessToast('Test 2'));
        ids.push(result.current.addSuccessToast('Test 3'));
      });

      expect(new Set(ids).size).toBe(3); // All IDs should be unique
    });
  });

  describe('Memory Management', () => {
    it('maintains reference equality for stable functions', () => {
      const { result, rerender } = renderHook(() => useToast());

      const firstRemoveToast = result.current.removeToast;

      rerender();

      expect(result.current.removeToast).toBe(firstRemoveToast);
    });

    it('maintains reference equality for toast type functions', () => {
      const { result, rerender } = renderHook(() => useToast());

      const firstAddSuccessToast = result.current.addSuccessToast;
      const firstAddErrorToast = result.current.addErrorToast;

      rerender();

      expect(result.current.addSuccessToast).toBe(firstAddSuccessToast);
      expect(result.current.addErrorToast).toBe(firstAddErrorToast);
    });
  });
});
