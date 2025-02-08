import { NextResponse } from 'next/server'
import { getUpcomingLaunches } from '@/app/lib/api'

export async function GET(): Promise<NextResponse> {
  try {
    const data = await getUpcomingLaunches()
    return NextResponse.json(data)
  } catch (error) {
    console.error('Error fetching upcoming launches:', error)
    return NextResponse.json(
      {
        error: 'Failed to fetch upcoming launches',
        details:
          error instanceof Error ? error.message : 'An unknown error occurred',
      },
      { status: 500 }
    )
  }
}
