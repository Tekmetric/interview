import { NextResponse } from 'next/server'
import { getPastLaunches } from '@/app/lib/api'

export async function GET(request: Request): Promise<NextResponse> {
  const { searchParams } = new URL(request.url)
  const page = Number.parseInt(searchParams.get('page') || '1', 10)
  const limit = Number.parseInt(searchParams.get('limit') || '5', 10)

  try {
    const data = await getPastLaunches(page, limit)
    return NextResponse.json(data)
  } catch (error: unknown) {
    console.error('Error fetching past launches:', error)
    return NextResponse.json(
      {
        error: 'Failed to fetch past launches',
        details:
          error instanceof Error ? error.message : 'An unknown error occurred',
      },
      { status: 500 }
    )
  }
}
