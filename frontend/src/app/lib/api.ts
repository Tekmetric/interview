const API_BASE_URL = 'https://api.spacexdata.com/v4'

async function fetchFromAPI(endpoint: string): Promise<unknown> {
  const res = await fetch(`${API_BASE_URL}${endpoint}`, {
    next: { revalidate: 60 },
  })

  if (!res.ok) {
    throw new Error(`HTTP error! status: ${res.status}`)
  }

  return res.json()
}

export async function getNextLaunch(): Promise<unknown> {
  try {
    return await fetchFromAPI('/launches/next')
  } catch (error) {
    throw new Error('Failed to fetch next launch')
  }
}

export async function getLaunchpad(id: string): Promise<unknown> {
  try {
    const response = await fetch(`${API_BASE_URL}/launchpads/${id}`, {
      next: { revalidate: 60 },
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const data = await response.json()
    return data
  } catch (error) {
    throw new Error('Failed to fetch launchpad')
  }
}

export async function getLatestLaunch(): Promise<unknown> {
  try {
    return await fetchFromAPI('/launches/latest')
  } catch (error) {
    throw new Error('Failed to fetch latest launch')
  }
}

export async function getUpcomingLaunches(): Promise<unknown> {
  try {
    return await fetchFromAPI('/launches/upcoming')
  } catch (error) {
    throw new Error('Failed to fetch upcoming launches')
  }
}

export async function getPastLaunches(page = 1, limit = 5): Promise<unknown> {
  try {
    const offset = (page - 1) * limit
    return await fetchFromAPI(
      `/launches/past?limit=${limit}&offset=${offset}&sort=date_utc&order=desc`
    )
  } catch (error) {
    throw new Error('Failed to fetch past launches')
  }
}

export async function getLaunchStats(): Promise<unknown> {
  try {
    const launches = (await fetchFromAPI('/launches')) as Array<{
      date_utc: string
      success: boolean
    }>

    const yearlyData = launches.reduce(
      (
        acc: Record<
          string,
          { year: number; successful: number; failed: number }
        >,
        launch
      ) => {
        const year = new Date(launch.date_utc).getFullYear()
        if (!acc[year]) {
          acc[year] = { year, successful: 0, failed: 0 }
        }
        if (launch.success) {
          acc[year].successful++
        } else {
          acc[year].failed++
        }
        return acc
      },
      {}
    )

    return Object.values(yearlyData)
  } catch (error) {
    throw new Error('Failed to fetch launch statistics')
  }
}

export async function getRockets(): Promise<unknown> {
  try {
    return await fetchFromAPI('/rockets')
  } catch (error) {
    throw new Error('Failed to fetch rockets')
  }
}
