import { type NextRequest, NextResponse } from 'next/server'
import { getLaunchpad } from '@/app/lib/api'


export async function GET(
  request: NextRequest,
  context: any // eslint-disable-line @typescript-eslint/no-explicit-any
): Promise<NextResponse> {
  const { params } = context as { params: { id: string | string[] } }
  const id = Array.isArray(params.id) ? params.id[0] : params.id

  try {
    const data = await getLaunchpad(id)
    return NextResponse.json(data, {
      status: 200,
      headers: {
        'Cache-Control': 'public, s-maxage=3600, stale-while-revalidate=1800',
      },
    })
  } catch (error: unknown) {
    console.error('Error fetching launchpad:', error)
    return NextResponse.json(
      {
        error: 'Failed to fetch launchpad',
        details: error instanceof Error ? error.message : 'Unknown error',
      },
      { status: 500 }
    )
  }
}
