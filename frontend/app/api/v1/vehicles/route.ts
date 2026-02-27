import { NextResponse } from 'next/server'
import { vehicles } from '@/lib/api/mock/data'

export async function GET() {
  return NextResponse.json(vehicles)
}
