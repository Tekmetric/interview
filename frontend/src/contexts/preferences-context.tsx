import { createContext, ReactNode, useCallback, useMemo } from 'react'
import { usePersistedState } from '@/hooks/use-persisted-state'
import type {
  UserPreferences,
  ColumnVisibility,
  SavedFilter,
} from '@/types/preferences'
import { DEFAULT_PREFERENCES } from '@/types/preferences'

interface PreferencesContextValue {
  preferences: UserPreferences
  updateColumnVisibility: (visibility: Partial<ColumnVisibility>) => void
  saveFilterPreset: (preset: Omit<SavedFilter, 'id' | 'createdAt'>) => void
  deleteFilterPreset: (presetId: string) => void
  setDefaultPreset: (presetId: string | undefined) => void
  getPresetById: (presetId: string) => SavedFilter | undefined
}

export const PreferencesContext = createContext<PreferencesContextValue | null>(null)

const STORAGE_KEY = 'tekboard-preferences'

export function PreferencesProvider({ children }: { children: ReactNode }) {
  const [preferences, setPreferences] = usePersistedState<UserPreferences>(
    STORAGE_KEY,
    DEFAULT_PREFERENCES,
  )

  const updateColumnVisibility = useCallback(
    (visibility: Partial<ColumnVisibility>) => {
      setPreferences((prev) => ({
        ...prev,
        columnVisibility: {
          ...prev.columnVisibility,
          ...visibility,
        },
      }))
    },
    [setPreferences],
  )

  const saveFilterPreset = useCallback(
    (preset: Omit<SavedFilter, 'id' | 'createdAt'>) => {
      const newPreset: SavedFilter = {
        ...preset,
        id: `preset-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`,
        createdAt: new Date().toISOString(),
      }

      setPreferences((prev) => ({
        ...prev,
        savedFilters: [...prev.savedFilters, newPreset],
      }))
    },
    [setPreferences],
  )

  const deleteFilterPreset = useCallback(
    (presetId: string) => {
      setPreferences((prev) => ({
        ...prev,
        savedFilters: prev.savedFilters.filter((p) => p.id !== presetId),
        // Clear default if it was the deleted preset
        defaultFilterPreset:
          prev.defaultFilterPreset === presetId ? undefined : prev.defaultFilterPreset,
      }))
    },
    [setPreferences],
  )

  const setDefaultPreset = useCallback(
    (presetId: string | undefined) => {
      setPreferences((prev) => ({
        ...prev,
        defaultFilterPreset: presetId,
      }))
    },
    [setPreferences],
  )

  const getPresetById = useCallback(
    (presetId: string): SavedFilter | undefined => {
      return preferences?.savedFilters?.find((p) => p.id === presetId)
    },
    [preferences],
  )

  const value = useMemo(
    () => ({
      preferences: preferences ?? DEFAULT_PREFERENCES,
      updateColumnVisibility,
      saveFilterPreset,
      deleteFilterPreset,
      setDefaultPreset,
      getPresetById,
    }),
    [
      preferences,
      updateColumnVisibility,
      saveFilterPreset,
      deleteFilterPreset,
      setDefaultPreset,
      getPresetById,
    ],
  )

  return <PreferencesContext.Provider value={value}>{children}</PreferencesContext.Provider>
}
