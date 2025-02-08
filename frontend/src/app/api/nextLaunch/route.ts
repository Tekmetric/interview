import { NextResponse } from 'next/server'
import { Launch, Launchpad } from '@/app/types'
import { getNextLaunch, getLaunchpad } from '@/app/lib/api'

export async function GET(): Promise<NextResponse> {
  try {
    const nextLaunch = (await getNextLaunch()) as Launch
    let launchpadData: Launchpad | null = null

    if (nextLaunch.launchpad) {
      try {
        launchpadData = (await getLaunchpad(nextLaunch.launchpad)) as Launchpad
      } catch (launchpadError) {
        console.error('Error fetching launchpad:', launchpadError)
      }
    }

    return NextResponse.json(
      { nextLaunch, launchpad: launchpadData },
      {
        status: 200,
        headers: {
          'Cache-Control': 'public, s-maxage=60, stale-while-revalidate=30',
        },
      }
    )
  } catch (error: unknown) {
    console.error('Error fetching next launch:', error)
    return NextResponse.json(
      {
        error: 'Failed to fetch next launch',
        details: error instanceof Error ? error.message : 'Unknown error',
      },
      { status: 500 }
    )
  }
}
