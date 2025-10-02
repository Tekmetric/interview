/* eslint-disable no-undef */
import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import { KanbanBoard } from '../kanban-board'
import { RO_STATUS } from '@shared/constants'
import { PreferencesContext } from '@/contexts/preferences-context'
import { DEFAULT_PREFERENCES } from '@/types/preferences'
import { SelectionProvider } from '@/contexts/selection-context'

describe('KanbanBoard', () => {
  const mockOrders = [
    { id: '1', status: RO_STATUS.NEW, customer: { name: 'John Doe' }, vehicle: { year: 2020, make: 'Toyota', model: 'Camry' }, services: [], approvedByCustomer: true, assignedTech: { id: 'tech-1', name: 'Tech 1'} },
    { id: '2', status: RO_STATUS.IN_PROGRESS, customer: { name: 'Jane Smith' }, vehicle: { year: 2021, make: 'Honda', model: 'Civic' }, services: [] },
  ]

  const customRender = (ui: React.ReactElement) => {
    return render(
      <PreferencesContext.Provider
        value={{
          preferences: { ...DEFAULT_PREFERENCES, savedFilters: [] },
          updateColumnVisibility: vi.fn(),
          saveFilterPreset: vi.fn(),
          deleteFilterPreset: vi.fn(),
          setDefaultPreset: vi.fn(),
          getPresetById: vi.fn(),
        }}
      >
        <SelectionProvider>{ui}</SelectionProvider>
      </PreferencesContext.Provider>
    )
  }

  it('should render the correct number of columns with the correct titles', () => {
    customRender(<KanbanBoard orders={mockOrders} onStatusChange={vi.fn()} />)

    expect(screen.getByText('New')).toBeInTheDocument()
    expect(screen.getByText('Awaiting Approval')).toBeInTheDocument()
    expect(screen.getByText('In Progress')).toBeInTheDocument()
    expect(screen.getByText('Waiting Parts')).toBeInTheDocument()
    expect(screen.getByText('Completed')).toBeInTheDocument()
  })
})
