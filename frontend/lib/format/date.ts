export function formatDateShortUS(input: string | Date): string {
  const date = typeof input === 'string' ? new Date(input) : input
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })
}
