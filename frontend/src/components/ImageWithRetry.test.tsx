import { act, fireEvent } from '@testing-library/react';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { renderWithProviders } from '../test/renderWithProviders';
import { ImageWithRetry } from './ImageWithRetry';

const SRC = 'https://example.test/avatar/1.jpeg';

describe('ImageWithRetry', () => {
  beforeEach(() => vi.useFakeTimers());
  afterEach(() => vi.useRealTimers());

  it('renders a plain image while it loads fine', () => {
    const { container } = renderWithProviders(<ImageWithRetry src={SRC} />);

    expect(container.querySelector('img')).toHaveAttribute('src', SRC);
  });

  it('swaps to a placeholder on error and retries once after the delay', () => {
    const { container } = renderWithProviders(<ImageWithRetry src={SRC} retryDelayMs={5000} />);

    fireEvent.error(container.querySelector('img')!);

    // Throttle window: placeholder instead of the broken-image glyph.
    expect(container.querySelector('img')).not.toBeInTheDocument();

    act(() => vi.advanceTimersByTime(5000));

    // The retry is a genuinely new request.
    expect(container.querySelector('img')).toHaveAttribute('src', `${SRC}?retry=1`);
  });

  it('gives up after the retry also fails', () => {
    const { container } = renderWithProviders(<ImageWithRetry src={SRC} retryDelayMs={5000} />);

    fireEvent.error(container.querySelector('img')!);
    act(() => vi.advanceTimersByTime(5000));
    fireEvent.error(container.querySelector('img')!);

    expect(container.querySelector('img')).not.toBeInTheDocument();
    act(() => vi.advanceTimersByTime(60_000));
    expect(container.querySelector('img')).not.toBeInTheDocument();
  });
});
