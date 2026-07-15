// @vitest-environment jsdom
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { renderHook, act, cleanup } from '@testing-library/react';
import { useDebouncedValue } from './useDebouncedValue';

describe('useDebouncedValue', () => {
  beforeEach(() => vi.useFakeTimers());
  afterEach(() => {
    vi.useRealTimers();
    cleanup();
  });

  it('delays updates until the value settles', () => {
    const { result, rerender } = renderHook(({ v }) => useDebouncedValue(v, 300), {
      initialProps: { v: 'a' },
    });
    expect(result.current[0]).toBe('a');

    rerender({ v: 'ab' });
    act(() => vi.advanceTimersByTime(299));
    expect(result.current[0]).toBe('a');

    act(() => vi.advanceTimersByTime(1));
    expect(result.current[0]).toBe('ab');
  });

  it('only the final value survives a burst of changes', () => {
    const { result, rerender } = renderHook(({ v }) => useDebouncedValue(v, 300), {
      initialProps: { v: 'a' },
    });
    rerender({ v: 'ab' });
    act(() => vi.advanceTimersByTime(100));
    rerender({ v: 'abc' });
    act(() => vi.advanceTimersByTime(300));
    expect(result.current[0]).toBe('abc');
  });

  it('flush() settles to the latest value immediately', () => {
    const { result, rerender } = renderHook(({ v }) => useDebouncedValue(v, 300), {
      initialProps: { v: 'a' },
    });
    rerender({ v: 'abc' });
    act(() => result.current[1]());
    expect(result.current[0]).toBe('abc');
  });
});
