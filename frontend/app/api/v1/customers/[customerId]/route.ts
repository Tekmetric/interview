import { NextResponse } from 'next/server'
import { customers } from '@/lib/api/mock/data'
import { customerIdParamsSchema, notFoundSchema } from '@/lib/api/contracts/schemas'

type RouteContext = {
  params: Promise<{ customerId: string }>
}

export async function GET(_: Request, context: RouteContext) {
  const { customerId } = customerIdParamsSchema.parse(await context.params)
  const customer = customers.find((item) => item.id === customerId)

  if (!customer) {
    return NextResponse.json(notFoundSchema.parse({ message: 'Customer not found' }), {
      status: 404,
    })
  }

  return NextResponse.json(customer)
}
