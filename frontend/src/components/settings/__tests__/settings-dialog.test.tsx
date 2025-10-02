
import { render, screen, fireEvent } from '@testing-library/react'
import { SettingsDialog } from '../settings-dialog'
import { PreferencesContext } from '@/contexts/preferences-context'
import type { PreferencesContextType } from '@/contexts/preferences-context'

// Mock the ColumnVisibilitySettings component
vi.mock('../column-visibility-settings', () => ({
  ColumnVisibilitySettings: () => <div>ColumnVisibilitySettings</div>,
}))

const mockOnOpenChange = vi.fn()

const mockPreferences: PreferencesContextType['preferences'] = {
  columnVisibility: {
    customerPhone: true,
    vehicleDetails: true,
    assignedTech: true,
    dueTime: true,
    services: true,
  },
}

const wrapper = ({ children }: { children: React.ReactNode }) => (
  <PreferencesContext.Provider
    value={{ preferences: mockPreferences, updateColumnVisibility: vi.fn() }}
  >
    {children}
  </PreferencesContext.Provider>
)

describe('SettingsDialog', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('should not render the dialog when open is false', () => {
    render(<SettingsDialog open={false} onOpenChange={mockOnOpenChange} />, { wrapper })
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument()
  })

  it('should render the dialog when open is true', () => {
    render(<SettingsDialog open={true} onOpenChange={mockOnOpenChange} />, { wrapper })

    expect(screen.getByRole('dialog')).toBeInTheDocument()
    expect(screen.getByText('Settings')).toBeInTheDocument()
    expect(
      screen.getByText('Customize your kanban board display preferences'),
    ).toBeInTheDocument()
    expect(screen.getByText('ColumnVisibilitySettings')).toBeInTheDocument()
  })

  it('should call onOpenChange when the dialog is closed', () => {
    render(<SettingsDialog open={true} onOpenChange={mockOnOpenChange} />, { wrapper })

    // Simulate closing the dialog (e.g., by clicking the close button or pressing Escape)
    // In this setup, the Dialog component from headless-ui/react calls onOpenChange
    // We can find the button by role and click it.
    const closeButton = screen.getByRole('button', { name: /close/i })
    fireEvent.click(closeButton)

    expect(mockOnOpenChange).toHaveBeenCalledWith(false)
  })
})
