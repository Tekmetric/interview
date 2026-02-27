import { describe, expect, it } from 'vitest'
import { formatUSD } from '@/lib/format/currency'
import { formatDateShortUS } from '@/lib/format/date'

describe('formatUSD', () => {
  it('formats whole numbers as USD currency', () => {
    expect(formatUSD(1234)).toBe('$1,234.00')
  })

  it('formats decimal values as USD currency', () => {
    expect(formatUSD(1234.5)).toBe('$1,234.50')
  })

  it('formats zero as USD currency', () => {
    expect(formatUSD(0)).toBe('$0.00')
  })
})

describe('formatDateShortUS', () => {
  it('formats ISO date strings in US short format', () => {
    expect(formatDateShortUS('2026-01-15T12:00:00.000Z')).toBe('Jan 15, 2026')
  })

  it('formats Date values in US short format', () => {
    expect(formatDateShortUS(new Date('2026-04-08T12:00:00.000Z'))).toBe('Apr 8, 2026')
  })
})
