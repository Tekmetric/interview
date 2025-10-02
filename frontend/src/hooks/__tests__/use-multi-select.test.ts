import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { renderHook, act } from '@testing-library/react'
import { SelectionProvider } from '@/contexts/selection-context'
import { useMultiSelect } from '@/hooks/use-multi-select'
import type { ReactNode } from 'react'
import { createElement } from 'react'

describe('useMultiSelect', () => {
  const wrapper = ({ children }: { children: ReactNode }) =>
    createElement(SelectionProvider, null, children)

  beforeEach(() => {
    // Clear any previous keyboard event listeners
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('should initialize with empty selection', () => {
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    expect(result.current.selection.selectedIds.size).toBe(0)
    expect(result.current.selection.isSelecting).toBe(false)
  })

  it('should toggle selection for a single item', () => {
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    act(() => {
      result.current.toggleSelection('RO-1')
    })

    expect(result.current.selection.selectedIds.has('RO-1')).toBe(true)
    expect(result.current.selection.isSelecting).toBe(true)
    expect(result.current.isSelected('RO-1')).toBe(true)

    act(() => {
      result.current.toggleSelection('RO-1')
    })

    expect(result.current.selection.selectedIds.has('RO-1')).toBe(false)
    expect(result.current.selection.isSelecting).toBe(false)
    expect(result.current.isSelected('RO-1')).toBe(false)
  })

  it('should select multiple items', () => {
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    act(() => {
      result.current.toggleSelection('RO-1')
      result.current.toggleSelection('RO-2')
      result.current.toggleSelection('RO-3')
    })

    expect(result.current.selection.selectedIds.size).toBe(3)
    expect(result.current.selection.selectedIds.has('RO-1')).toBe(true)
    expect(result.current.selection.selectedIds.has('RO-2')).toBe(true)
    expect(result.current.selection.selectedIds.has('RO-3')).toBe(true)
    expect(result.current.selection.isSelecting).toBe(true)
  })

  it('should clear all selections', () => {
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    act(() => {
      result.current.toggleSelection('RO-1')
      result.current.toggleSelection('RO-2')
    })

    expect(result.current.selection.selectedIds.size).toBe(2)

    act(() => {
      result.current.clearSelection()
    })

    expect(result.current.selection.selectedIds.size).toBe(0)
    expect(result.current.selection.isSelecting).toBe(false)
  })

  it('should check if an item is selected', () => {
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    expect(result.current.isSelected('RO-1')).toBe(false)

    act(() => {
      result.current.toggleSelection('RO-1')
    })

    expect(result.current.isSelected('RO-1')).toBe(true)
    expect(result.current.isSelected('RO-2')).toBe(false)
  })

  it('should enter selection mode when first item selected', () => {
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    expect(result.current.selection.isSelecting).toBe(false)

    act(() => {
      result.current.toggleSelection('RO-1')
    })

    expect(result.current.selection.isSelecting).toBe(true)
  })

  it('should exit selection mode when all items deselected', () => {
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    act(() => {
      result.current.toggleSelection('RO-1')
      result.current.toggleSelection('RO-2')
    })

    expect(result.current.selection.isSelecting).toBe(true)

    act(() => {
      result.current.toggleSelection('RO-1')
      result.current.toggleSelection('RO-2')
    })

    expect(result.current.selection.isSelecting).toBe(false)
  })

  it('should handle toggling the same item multiple times', () => {
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    act(() => {
      result.current.toggleSelection('RO-1')
    })
    expect(result.current.isSelected('RO-1')).toBe(true)

    act(() => {
      result.current.toggleSelection('RO-1')
    })
    expect(result.current.isSelected('RO-1')).toBe(false)

    act(() => {
      result.current.toggleSelection('RO-1')
    })
    expect(result.current.isSelected('RO-1')).toBe(true)
  })

  it('should maintain selection state across multiple operations', () => {
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    act(() => {
      result.current.toggleSelection('RO-1')
      result.current.toggleSelection('RO-2')
      result.current.toggleSelection('RO-3')
    })

    expect(result.current.selection.selectedIds.size).toBe(3)

    act(() => {
      result.current.toggleSelection('RO-2')
    })

    expect(result.current.selection.selectedIds.size).toBe(2)
    expect(result.current.isSelected('RO-1')).toBe(true)
    expect(result.current.isSelected('RO-2')).toBe(false)
    expect(result.current.isSelected('RO-3')).toBe(true)
  })

  it('should handle empty string IDs', () => {
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    act(() => {
      result.current.toggleSelection('')
    })

    expect(result.current.selection.selectedIds.has('')).toBe(true)
    expect(result.current.isSelected('')).toBe(true)
  })

  it('should select all items when selectAll is called', () => {
    const { result } = renderHook(() => useMultiSelect(), { wrapper })

    act(() => {
      result.current.selectAll(['RO-1', 'RO-2', 'RO-3'])
    })

    expect(result.current.selection.selectedIds.size).toBe(3)
    expect(result.current.isSelected('RO-1')).toBe(true)
    expect(result.current.isSelected('RO-2')).toBe(true)
    expect(result.current.isSelected('RO-3')).toBe(true)
  })
})
