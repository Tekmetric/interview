import { NextResponse } from 'next/server'
import { getLatestLaunch } from '@/app/lib/api'

export async function GET(): Promise<NextResponse> {
  try {
    const data = await getLatestLaunch()
    return NextResponse.json(data)
  } catch (error) {
    console.error('Error fetching latest launch:', error)
    return NextResponse.json(
      { error: 'Failed to fetch latest launch' },
      { status: 500 }
    )
  }
}
