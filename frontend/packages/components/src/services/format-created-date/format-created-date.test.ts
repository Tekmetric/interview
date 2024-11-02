import { formatDistance } from 'date-fns/formatDistance'

import { formatCreatedDate } from './format-created-date'

// Mock the formatDistance function
jest.mock('date-fns/formatDistance', () => ({
  formatDistance: jest.fn()
}))

const formatDistanceMock = formatDistance as jest.Mock

describe('formatCreatedDate', () => {
  it('should format the date correctly', () => {
    const mockDate = '2023-01-01T00:00:00Z'
    const mockFormattedDate = '3 days ago'

    formatDistanceMock.mockReturnValue(mockFormattedDate)

    const result = formatCreatedDate(mockDate)

    expect(formatDistance).toHaveBeenCalledWith(mockDate, expect.any(Date), {
      addSuffix: true
    })
    expect(result).toBe(mockFormattedDate)
  })

  it('should handle invalid date', () => {
    const mockDate = 'invalid-date'
    formatDistanceMock.mockImplementation(() => {
      throw new Error('Invalid time value')
    })

    expect(() => formatCreatedDate(mockDate)).toThrow('Invalid time value')
  })
})
