import { NextResponse } from 'next/server'
import { vehicles } from '@/lib/api/mock/data'
import { notFoundSchema, vehicleIdParamsSchema } from '@/lib/api/contracts/schemas'

type RouteContext = {
  params: Promise<{ vehicleId: string }>
}

export async function GET(_: Request, context: RouteContext) {
  const { vehicleId } = vehicleIdParamsSchema.parse(await context.params)
  const vehicle = vehicles.find((item) => item.id === vehicleId)

  if (!vehicle) {
    return NextResponse.json(notFoundSchema.parse({ message: 'Vehicle not found' }), {
      status: 404,
    })
  }

  return NextResponse.json(vehicle)
}
