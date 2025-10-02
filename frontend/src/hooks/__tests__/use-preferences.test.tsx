
import React from 'react'
import { renderHook } from '@testing-library/react'
import { usePreferences } from '../use-preferences'
import { PreferencesContext, PreferencesProvider } from '@/contexts/preferences-context'
import type { PreferencesContextType } from '@/contexts/preferences-context'

describe('usePreferences', () => {
  it('should throw an error when used outside of a PreferencesProvider', () => {
    // Suppress console.error for this test
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

    expect(() => renderHook(() => usePreferences())).toThrow(
      'usePreferences must be used within PreferencesProvider',
    )

    consoleErrorSpy.mockRestore()
  })

  it('should return the context value when used within a PreferencesProvider', () => {
    const mockValue: PreferencesContextType = {
      preferences: {
        columnVisibility: {
          customerPhone: true,
          vehicleDetails: true,
          assignedTech: true,
          dueTime: true,
          services: true,
        },
      },
      updateColumnVisibility: vi.fn(),
    }

    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <PreferencesContext.Provider value={mockValue}>{children}</PreferencesContext.Provider>
    )

    const { result } = renderHook(() => usePreferences(), { wrapper })

    expect(result.current).toEqual(mockValue)
  })
})
