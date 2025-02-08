export interface Launch {
  id: string
  name: string
  date_utc: string
  date_unix: number
  success?: boolean
  rocket?: string
  launchpad?: string
  links?: {
    patch?: {
      small?: string
    }
    youtube_id?: string
    webcast?: string
  }
  details?: string
}

export interface Launchpad {
  id: string
  name: string
  full_name: string
  latitude: number
  longitude: number
}

export interface LaunchSite {
  id: string
  name: string
  latitude: number
  longitude: number
}

export interface Rocket {
  id: string
  name: string
  description: string
  height: {
    meters: number
    feet: number
  }
  diameter: {
    meters: number
    feet: number
  }
  mass: {
    kg: number
    lb: number
  }
  first_stage: {
    engines: number
    fuel_amount_tons: number
    burn_time_sec: number
    thrust_sea_level: {
      kN: number
    }
    thrust_vacuum: {
      kN: number
    }
    reusable: boolean
  }
  second_stage: {
    engines: number
    fuel_amount_tons: number
    burn_time_sec: number
    thrust: {
      kN: number
    }
    reusable: boolean
  }
  engines: {
    number: number
    type: string
    version: string
    layout: string
    thrust_sea_level: {
      kN: number
    }
    thrust_vacuum: {
      kN: number
    }
    propellant_1: string
    propellant_2: string
  }
  landing_legs: {
    number: number
    material: string
  }
  payload_weights: Array<{
    id: string
    name: string
    kg: number
    lb: number
  }>
  active: boolean
  stages: number
  boosters: number
  cost_per_launch: number
  success_rate_pct: number
  first_flight: string
  country: string
  company: string
  wikipedia: string
  flickr_images: string[]
}

export interface LaunchStats {
  year: number
  successful: number
  failed: number
}
