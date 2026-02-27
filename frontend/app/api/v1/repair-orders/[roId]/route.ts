import { NextResponse } from 'next/server'
import { repairOrders } from '@/lib/api/mock/data'
import { notFoundSchema, repairOrderIdParamsSchema } from '@/lib/api/contracts/schemas'

type RouteContext = {
  params: Promise<{ roId: string }>
}

export async function GET(_: Request, context: RouteContext) {
  const { roId } = repairOrderIdParamsSchema.parse(await context.params)
  const repairOrder = repairOrders.find((item) => item.id === roId)

  if (!repairOrder) {
    return NextResponse.json(notFoundSchema.parse({ message: 'Repair order not found' }), {
      status: 404,
    })
  }

  return NextResponse.json(repairOrder)
}
