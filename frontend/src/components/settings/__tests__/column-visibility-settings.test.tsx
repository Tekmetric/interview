import { type ReactNode } from 'react'
import { render, screen, fireEvent } from '@testing-library/react'
import { ColumnVisibilitySettings } from '../column-visibility-settings'
import { PreferencesContext } from '@/contexts/preferences-context'
import type { UserPreferences } from '@/types/preferences'

const mockUpdateColumnVisibility = vi.fn()

const mockPreferences: UserPreferences = {
  columnVisibility: {
    customerPhone: true,
    vehicleDetails: false,
    assignedTech: true,
    dueTime: false,
    services: true,
  },
  savedFilters: [],
}

const wrapper = ({ children }: { children: ReactNode }) => (
  <PreferencesContext.Provider
    value={{
      preferences: mockPreferences,
      updateColumnVisibility: mockUpdateColumnVisibility,
      saveFilterPreset: vi.fn(),
      deleteFilterPreset: vi.fn(),
      setDefaultPreset: vi.fn(),
      getPresetById: vi.fn(),
    }}
  >
    {children}
  </PreferencesContext.Provider>
)

describe('ColumnVisibilitySettings', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should render the list of columns with correct checked status', () => {
    render(<ColumnVisibilitySettings />, { wrapper })

    expect(screen.getByLabelText('Customer Phone')).toBeChecked()
    expect(screen.getByLabelText('Vehicle Details (Year/Make/Model)')).not.toBeChecked()
    expect(screen.getByLabelText('Assigned Technician')).toBeChecked()
    expect(screen.getByLabelText('Due Time')).not.toBeChecked()
    expect(screen.getByLabelText('Services')).toBeChecked()
  })

  it('should call updateColumnVisibility when a checkbox is clicked', () => {
    render(<ColumnVisibilitySettings />, { wrapper })

    const vehicleDetailsCheckbox = screen.getByLabelText(
      'Vehicle Details (Year/Make/Model)',
    )
    fireEvent.click(vehicleDetailsCheckbox)

    expect(mockUpdateColumnVisibility).toHaveBeenCalledWith({ vehicleDetails: true })

    const customerPhoneCheckbox = screen.getByLabelText('Customer Phone')
    fireEvent.click(customerPhoneCheckbox)

    expect(mockUpdateColumnVisibility).toHaveBeenCalledWith({ customerPhone: false })
  })

  it('should render the title and description', () => {
    render(<ColumnVisibilitySettings />, { wrapper })

    expect(screen.getByText('Card Display Options')).toBeInTheDocument()
    expect(
      screen.getByText('Choose which fields to show on kanban cards'),
    ).toBeInTheDocument()
  })
})
