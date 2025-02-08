import { NextResponse } from 'next/server'
import { getRockets } from '@/app/lib/api'

export async function GET(): Promise<NextResponse> {
  try {
    const rockets = await getRockets()
    return NextResponse.json(rockets, {
      status: 200,
      headers: {
        'Cache-Control': 'public, s-maxage=3600, stale-while-revalidate=1800',
      },
    })
  } catch (error: unknown) {
    console.error('Error fetching rockets:', error)
    return NextResponse.json(
      {
        error: 'Failed to fetch rockets',
        details:
          error instanceof Error ? error.message : 'An unknown error occurred',
      },
      { status: 500 }
    )
  }
}
