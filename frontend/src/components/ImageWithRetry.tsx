import { useEffect, useState, type ComponentPropsWithoutRef } from 'react';
import styled from 'styled-components';

import { PortalIcon } from './icons';

const Placeholder = styled.div`
  display: grid;
  place-items: center;
  aspect-ratio: 1;
  background: ${({ theme }) => theme.colors.surfaceHover};
  color: ${({ theme }) => theme.colors.textMuted};
`;

type LoadState = 'loading' | 'waiting-retry' | 'failed';

interface ImageWithRetryProps extends ComponentPropsWithoutRef<'img'> {
  src: string;
  retryDelayMs?: number;
}

// The API's CDN rate-limits bursts of image requests: it answers 429 with a
// text body, which the browser blocks (ORB — not an image) and reports as a
// broken image. Those failures are transient (Retry-After: 10), so after an
// error this waits out the throttle window and retries once; a themed
// placeholder covers both the wait and the permanent-failure case instead of
// the browser's broken-image glyph.
export function ImageWithRetry({
  src,
  retryDelayMs = 12_000,
  className,
  alt = '',
  ...imgProps
}: ImageWithRetryProps) {
  const [state, setState] = useState<LoadState>('loading');
  const [isRetry, setIsRetry] = useState(false);

  useEffect(() => {
    if (state !== 'waiting-retry') {
      return undefined;
    }
    const timer = window.setTimeout(() => {
      setIsRetry(true);
      setState('loading');
    }, retryDelayMs);
    // Virtualized rows unmount mid-wait; the cleanup cancels the pending retry.
    return () => window.clearTimeout(timer);
  }, [state, retryDelayMs]);

  if (state !== 'loading') {
    return (
      <Placeholder className={className} aria-hidden="true">
        <PortalIcon size={48} />
      </Placeholder>
    );
  }

  return (
    <img
      {...imgProps}
      className={className}
      alt={alt}
      // The query param makes the retry a genuinely new request instead of a
      // replay the browser may satisfy from the failed attempt.
      src={isRetry ? `${src}?retry=1` : src}
      onError={() => setState(isRetry ? 'failed' : 'waiting-retry')}
    />
  );
}
