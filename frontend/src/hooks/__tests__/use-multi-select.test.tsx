
import React from 'react'
import { renderHook, act } from '@testing-library/react'
import { useMultiSelect, useMultiSelectKeyboard } from '../use-multi-select'
import { SelectionProvider, SelectionContext } from '@/contexts/selection-context'
import type { SelectionContextType } from '@/contexts/selection-context'

describe('useMultiSelect', () => {
  it('should throw an error if used outside of SelectionProvider', () => {
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
    expect(() => renderHook(() => useMultiSelect())).toThrow(
      'useMultiSelect must be used within SelectionProvider',
    )
    consoleErrorSpy.mockRestore()
  })

  it('should return the selection context', () => {
    const wrapper = ({ children }: { children: React.ReactNode }) => (
      <SelectionProvider>{children}</SelectionProvider>
    )
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    expect(result.current).toHaveProperty('selection')
    expect(result.current).toHaveProperty('isSelected')
    expect(result.current).toHaveProperty('toggleSelection')
    expect(result.current).toHaveProperty('selectAll')
    expect(result.current).toHaveProperty('clearSelection')
  })
})

describe('useMultiSelectKeyboard', () => {
  const mockSelectAll = vi.fn()
  const mockClearSelection = vi.fn()

  const wrapper = ({ children }: { children: React.ReactNode }) => (
    <SelectionContext.Provider
      value={{
        selection: {
          selectedIds: new Set(),
          isSelecting: false,
          lastSelectedId: null,
        },
        isSelected: () => false,
        toggleSelection: () => {},
        selectRange: () => {},
        selectAll: mockSelectAll,
        clearSelection: mockClearSelection,
      }}
    >
      {children}
    </SelectionContext.Provider>
  )

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should call selectAll when Cmd+A is pressed', () => {
    const orderIds = ['1', '2', '3']
    renderHook(() => useMultiSelectKeyboard(orderIds), { wrapper })

    const event = new KeyboardEvent('keydown', { key: 'a', metaKey: true })
    act(() => {
      window.dispatchEvent(event)
    })

    expect(mockSelectAll).toHaveBeenCalledWith(orderIds)
  })

  it('should call selectAll when Ctrl+A is pressed', () => {
    const orderIds = ['1', '2', '3']
    renderHook(() => useMultiSelectKeyboard(orderIds), { wrapper })

    const event = new KeyboardEvent('keydown', { key: 'a', ctrlKey: true })
    act(() => {
      window.dispatchEvent(event)
    })

    expect(mockSelectAll).toHaveBeenCalledWith(orderIds)
  })

  it('should call clearSelection when Escape is pressed', () => {
    const orderIds = ['1', '2', '3']
    renderHook(() => useMultiSelectKeyboard(orderIds), { wrapper })

    const event = new KeyboardEvent('keydown', { key: 'Escape' })
    act(() => {
      window.dispatchEvent(event)
    })

    expect(mockClearSelection).toHaveBeenCalled()
  })

  it('should remove event listener on unmount', () => {
    const orderIds = ['1', '2', '3']
    const removeEventListenerSpy = vi.spyOn(window, 'removeEventListener')
    const { unmount } = renderHook(() => useMultiSelectKeyboard(orderIds), { wrapper })

    unmount()

    expect(removeEventListenerSpy).toHaveBeenCalledWith('keydown', expect.any(Function))
  })
})
