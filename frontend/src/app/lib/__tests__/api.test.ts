import {
  getNextLaunch,
  getLaunchpad,
  getLatestLaunch,
  getUpcomingLaunches,
  getPastLaunches,
  getLaunchStats,
  getRockets,
} from '../api'

const API_BASE_URL = 'https://api.spacexdata.com/v4'

if (!global.fetch) {
  ;(global as any).fetch = jest.fn()
}

describe('API functions', () => {
  afterEach(() => {
    jest.restoreAllMocks()
  })

  describe('fetchFromAPI', () => {
    it('returns JSON data when response is ok', async () => {
      const dummyData = { id: '123', name: 'Test' }
      const fetchSpy = jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: true,
        json: async () => dummyData,
      } as Response)

      const result = await getLatestLaunch()
      expect(fetchSpy).toHaveBeenCalledWith(`${API_BASE_URL}/launches/latest`, {
        next: { revalidate: 60 },
      })
      expect(result).toEqual(dummyData)
    })

    it('throws an error when response is not ok', async () => {
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: false,
        status: 500,
      } as Response)
      await expect(getLatestLaunch()).rejects.toThrow(
        'Failed to fetch latest launch'
      )
    })

    it('propagates fetch errors', async () => {
      jest
        .spyOn(global, 'fetch')
        .mockRejectedValueOnce(new Error('Network error'))
      await expect(getLatestLaunch()).rejects.toThrow(
        'Failed to fetch latest launch'
      )
    })
  })

  describe('getNextLaunch', () => {
    it('returns next launch data when fetch is successful', async () => {
      const dummyLaunch = { id: '1', name: 'Next Launch' }
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: true,
        json: async () => dummyLaunch,
      } as Response)

      const result = await getNextLaunch()
      expect(result).toEqual(dummyLaunch)
      expect(global.fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/launches/next`,
        {
          next: { revalidate: 60 },
        }
      )
    })

    it('throws a custom error when fetch fails', async () => {
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: false,
        status: 404,
      } as Response)
      await expect(getNextLaunch()).rejects.toThrow(
        'Failed to fetch next launch'
      )
    })
  })

  describe('getLaunchpad', () => {
    const launchpadId = 'abcd1234'
    it('returns launchpad data when fetch is successful', async () => {
      const dummyLaunchpad = { id: launchpadId, name: 'Test Launchpad' }
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: true,
        json: async () => dummyLaunchpad,
      } as Response)

      const result = await getLaunchpad(launchpadId)
      expect(result).toEqual(dummyLaunchpad)
      expect(global.fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/launchpads/${launchpadId}`,
        {
          next: { revalidate: 60 },
        }
      )
    })

    it('throws a custom error when fetch fails', async () => {
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: false,
        status: 500,
      } as Response)
      await expect(getLaunchpad(launchpadId)).rejects.toThrow(
        `Failed to fetch launchpad`
      )
    })

    it('handles invalid launchpad ID', async () => {
      await expect(getLaunchpad('')).rejects.toThrow(
        'Failed to fetch launchpad'
      )
    })
  })

  describe('getLatestLaunch', () => {
    it('returns latest launch data when fetch is successful', async () => {
      const dummyLaunch = { id: 'latest', name: 'Latest Launch' }
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: true,
        json: async () => dummyLaunch,
      } as Response)

      const result = await getLatestLaunch()
      expect(result).toEqual(dummyLaunch)
      expect(global.fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/launches/latest`,
        {
          next: { revalidate: 60 },
        }
      )
    })

    it('throws a custom error when fetch fails', async () => {
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: false,
        status: 404,
      } as Response)
      await expect(getLatestLaunch()).rejects.toThrow(
        'Failed to fetch latest launch'
      )
    })
  })

  describe('getUpcomingLaunches', () => {
    it('returns upcoming launches data when fetch is successful', async () => {
      const dummyUpcomingLaunches = [{ id: 'u1' }, { id: 'u2' }]
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: true,
        json: async () => dummyUpcomingLaunches,
      } as Response)

      const result = await getUpcomingLaunches()
      expect(result).toEqual(dummyUpcomingLaunches)
      expect(global.fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/launches/upcoming`,
        {
          next: { revalidate: 60 },
        }
      )
    })

    it('throws a custom error when fetch fails', async () => {
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: false,
        status: 500,
      } as Response)
      await expect(getUpcomingLaunches()).rejects.toThrow(
        'Failed to fetch upcoming launches'
      )
    })
  })

  describe('getPastLaunches', () => {
    it('returns past launches with correct query parameters', async () => {
      const dummyPastLaunches = [{ id: 'p1' }, { id: 'p2' }]
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: true,
        json: async () => dummyPastLaunches,
      } as Response)

      const page = 2
      const limit = 3
      const offset = (page - 1) * limit
      const result = await getPastLaunches(page, limit)
      expect(result).toEqual(dummyPastLaunches)
      expect(global.fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/launches/past?limit=${limit}&offset=${offset}&sort=date_utc&order=desc`,
        { next: { revalidate: 60 } }
      )
    })

    it('throws a custom error when fetch fails', async () => {
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: false,
        status: 404,
      } as Response)
      await expect(getPastLaunches()).rejects.toThrow(
        'Failed to fetch past launches'
      )
    })

    it('uses default values for page and limit when not provided', async () => {
      const dummyPastLaunches = [{ id: 'p1' }, { id: 'p2' }]
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: true,
        json: async () => dummyPastLaunches,
      } as Response)

      await getPastLaunches()
      expect(global.fetch).toHaveBeenCalledWith(
        `${API_BASE_URL}/launches/past?limit=5&offset=0&sort=date_utc&order=desc`,
        { next: { revalidate: 60 } }
      )
    })
  })

  describe('getLaunchStats', () => {
    it('processes launch statistics correctly', async () => {
      const dummyLaunches = [
        { date_utc: '2020-05-01T00:00:00.000Z', success: true },
        { date_utc: '2020-06-01T00:00:00.000Z', success: false },
        { date_utc: '2021-01-01T00:00:00.000Z', success: true },
      ]
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: true,
        json: async () => dummyLaunches,
      } as Response)

      const result = await getLaunchStats()
      expect(result).toEqual(
        expect.arrayContaining([
          { year: 2020, successful: 1, failed: 1 },
          { year: 2021, successful: 1, failed: 0 },
        ])
      )
      expect(global.fetch).toHaveBeenCalledWith(`${API_BASE_URL}/launches`, {
        next: { revalidate: 60 },
      })
    })

    it('throws a custom error when fetch fails', async () => {
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: false,
        status: 400,
      } as Response)
      await expect(getLaunchStats()).rejects.toThrow(
        'Failed to fetch launch statistics'
      )
    })

    it('handles empty launch data', async () => {
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: true,
        json: async () => [],
      } as Response)

      const result = await getLaunchStats()
      expect(result).toEqual([])
    })

    it('sorts the result by year', async () => {
      const dummyLaunches = [
        { date_utc: '2021-01-01T00:00:00.000Z', success: true },
        { date_utc: '2020-01-01T00:00:00.000Z', success: true },
        { date_utc: '2022-01-01T00:00:00.000Z', success: false },
      ]
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: true,
        json: async () => dummyLaunches,
      } as Response)

      const result = await getLaunchStats()
      expect(result).toEqual([
        { year: 2020, successful: 1, failed: 0 },
        { year: 2021, successful: 1, failed: 0 },
        { year: 2022, successful: 0, failed: 1 },
      ])
    })
  })

  describe('getRockets', () => {
    it('returns rockets data when fetch is successful', async () => {
      const dummyRockets = [{ id: 'r1' }, { id: 'r2' }]
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: true,
        json: async () => dummyRockets,
      } as Response)

      const result = await getRockets()
      expect(result).toEqual(dummyRockets)
      expect(global.fetch).toHaveBeenCalledWith(`${API_BASE_URL}/rockets`, {
        next: { revalidate: 60 },
      })
    })

    it('throws a custom error when fetch fails', async () => {
      jest.spyOn(global, 'fetch').mockResolvedValueOnce({
        ok: false,
        status: 500,
      } as Response)
      await expect(getRockets()).rejects.toThrow('Failed to fetch rockets')
    })
  })
})
