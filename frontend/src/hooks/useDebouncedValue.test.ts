import { act, renderHook } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { useDebouncedValue } from './useDebouncedValue';

describe('useDebouncedValue', () => {
  beforeEach(() => vi.useFakeTimers());
  afterEach(() => vi.useRealTimers());

  it('publishes the value only after it stops changing for the delay', () => {
    const { result, rerender } = renderHook(({ value }) => useDebouncedValue(value, 300), {
      initialProps: { value: 'r' },
    });
    expect(result.current).toBe('r');

    rerender({ value: 'ri' });
    act(() => vi.advanceTimersByTime(299));
    // Still within the delay window — the old value holds.
    expect(result.current).toBe('r');

    // Typing again resets the timer.
    rerender({ value: 'rick' });
    act(() => vi.advanceTimersByTime(299));
    expect(result.current).toBe('r');

    act(() => vi.advanceTimersByTime(1));
    expect(result.current).toBe('rick');
  });
});
