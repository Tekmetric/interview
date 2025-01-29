export const API_BASE_URL = 'https://api.spacexdata.com/v4'

export const REVALIDATION_INTERVALS = {
  NEXT_LAUNCH: 60, // 1 minute
  LATEST_LAUNCH: 300, // 5 minutes
  UPCOMING_LAUNCHES: 300, // 5 minutes
  PAST_LAUNCHES: 900, // 15 minutes
  LAUNCH_STATS: 1800, // 30 minutes
  ROCKETS: 3600, // 1 hour
} as const

export const DEFAULT_PAGE_SIZE = 5

export const LAUNCH_SITES = [
  {
    id: 'ccafs',
    name: 'Cape Canaveral',
    latitude: 28.4922,
    longitude: -80.5217,
  },
  {
    id: 'vafb',
    name: 'Vandenberg',
    latitude: 34.632,
    longitude: -120.6107,
  },
  {
    id: 'stls',
    name: 'Starbase',
    latitude: 25.9971,
    longitude: -97.1554,
  },
] as const

export const MAP_CONFIG = {
  DEFAULT_CENTER: [35.8283, -98.5795],
  DEFAULT_ZOOM: 4,
  MAX_ZOOM: 19,
} as const
