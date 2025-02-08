import { NextResponse } from 'next/server'
import { getLaunchStats } from '@/app/lib/api'

export async function GET(): Promise<NextResponse> {
  try {
    const data = await getLaunchStats()
    return NextResponse.json(data)
  } catch (error) {
    console.error('Error fetching launch statistics:', error)
    return NextResponse.json(
      {
        error: 'Failed to fetch launch statistics',
        details:
          error instanceof Error ? error.message : 'Unknown error occurred',
      },
      { status: 500 }
    )
  }
}
