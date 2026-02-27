import { NextResponse } from 'next/server'
import { repairOrders } from '@/lib/api/mock/data'

export async function GET() {
  return NextResponse.json(repairOrders)
}
