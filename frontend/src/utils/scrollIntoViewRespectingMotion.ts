export function scrollIntoViewRespectingMotion(
  element: HTMLElement | null | undefined,
  options?: Omit<ScrollIntoViewOptions, 'behavior'>
) {
  if (!element) {
    return;
  }

  const prefersReducedMotion = window.matchMedia(
    '(prefers-reduced-motion: reduce)'
  ).matches;

  element.scrollIntoView({
    block: 'start',
    ...options,
    behavior: prefersReducedMotion ? 'auto' : 'smooth',
  });
}
