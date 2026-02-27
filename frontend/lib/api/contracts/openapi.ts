import { OpenApiGeneratorV3, OpenAPIRegistry } from '@asteasolutions/zod-to-openapi'
import { z } from 'zod'
import {
  customerIdParamsSchema,
  customerSchema,
  dashboardKpisSchema,
  healthSchema,
  notFoundSchema,
  repairOrderIdParamsSchema,
  repairOrderSchema,
  vehicleIdParamsSchema,
  vehicleSchema,
} from './schemas'

const registry = new OpenAPIRegistry()

registry.registerPath({
  method: 'get',
  path: '/api/v1/health',
  summary: 'Health check',
  responses: {
    200: {
      description: 'Service status',
      content: {
        'application/json': { schema: healthSchema },
      },
    },
  },
})

registry.registerPath({
  method: 'get',
  path: '/api/v1/customers',
  summary: 'List customers',
  responses: {
    200: {
      description: 'Customers',
      content: {
        'application/json': { schema: z.array(customerSchema) },
      },
    },
  },
})

registry.registerPath({
  method: 'get',
  path: '/api/v1/customers/{customerId}',
  summary: 'Get customer',
  request: { params: customerIdParamsSchema },
  responses: {
    200: {
      description: 'Customer',
      content: {
        'application/json': { schema: customerSchema },
      },
    },
    404: {
      description: 'Not found',
      content: {
        'application/json': { schema: notFoundSchema },
      },
    },
  },
})

registry.registerPath({
  method: 'get',
  path: '/api/v1/vehicles',
  summary: 'List vehicles',
  responses: {
    200: {
      description: 'Vehicles',
      content: {
        'application/json': { schema: z.array(vehicleSchema) },
      },
    },
  },
})

registry.registerPath({
  method: 'get',
  path: '/api/v1/vehicles/{vehicleId}',
  summary: 'Get vehicle',
  request: { params: vehicleIdParamsSchema },
  responses: {
    200: {
      description: 'Vehicle',
      content: {
        'application/json': { schema: vehicleSchema },
      },
    },
    404: {
      description: 'Not found',
      content: {
        'application/json': { schema: notFoundSchema },
      },
    },
  },
})

registry.registerPath({
  method: 'get',
  path: '/api/v1/repair-orders',
  summary: 'List repair orders',
  responses: {
    200: {
      description: 'Repair orders',
      content: {
        'application/json': { schema: z.array(repairOrderSchema) },
      },
    },
  },
})

registry.registerPath({
  method: 'get',
  path: '/api/v1/repair-orders/{roId}',
  summary: 'Get repair order',
  request: { params: repairOrderIdParamsSchema },
  responses: {
    200: {
      description: 'Repair order',
      content: {
        'application/json': { schema: repairOrderSchema },
      },
    },
    404: {
      description: 'Not found',
      content: {
        'application/json': { schema: notFoundSchema },
      },
    },
  },
})

registry.registerPath({
  method: 'get',
  path: '/api/v1/dashboard/kpis',
  summary: 'Get dashboard KPIs',
  responses: {
    200: {
      description: 'KPIs',
      content: {
        'application/json': { schema: dashboardKpisSchema },
      },
    },
  },
})

const generator = new OpenApiGeneratorV3(registry.definitions)

export const openApiSpec = generator.generateDocument({
  openapi: '3.0.3',
  info: {
    title: 'Interview Shop API',
    version: '1.0.0',
    description: 'Simple Tekmetric-style interview API',
  },
  servers: [{ url: 'http://localhost:3000' }],
})
