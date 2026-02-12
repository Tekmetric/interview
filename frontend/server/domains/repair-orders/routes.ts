import { Router } from 'express'
import {
  getAllRepairOrders,
  getRepairOrderById,
  createRepairOrder,
  updateRepairOrder,
  deleteRepairOrder,
  getOverdueOrders,
  getRecentOrders,
} from './repository'
import { canTransition, ALLOWED_TRANSITIONS } from '@shared/transitions'
import {
  createRepairOrderSchema,
  updateRepairOrderSchema,
  repairOrderFiltersSchema,
} from '@shared/validation'
import { validate } from '@server/core/middleware/validate'
import { HTTP_STATUS, ERROR_CODES } from '@server/constants'

const router = Router()

router.get('/repairOrders/overdue', (req, res) => {
  const limit = req.query.limit ? parseInt(req.query.limit as string, 10) : 5
  const orders = getOverdueOrders(limit)
  res.json(orders)
})

router.get('/repairOrders/recent', (req, res) => {
  const limit = req.query.limit ? parseInt(req.query.limit as string, 10) : 5
  const orders = getRecentOrders(limit)
  res.json(orders)
})

router.get('/repairOrders', validate(repairOrderFiltersSchema, 'query'), (req, res) => {
  const { status, tech, priority, search } = req.query

  let orders = getAllRepairOrders()

  if (status) {
    orders = orders.filter((o) => o.status === status)
  }
  if (tech) {
    orders = orders.filter((o) => o.assignedTech?.id === tech)
  }
  if (priority) {
    orders = orders.filter((o) => o.priority === priority)
  }
  if (search) {
    const query = (search as string).toLowerCase()
    orders = orders.filter(
      (o) =>
        o.id.toLowerCase().includes(query) ||
        o.customer.name.toLowerCase().includes(query) ||
        `${o.vehicle.year} ${o.vehicle.make} ${o.vehicle.model}`
          .toLowerCase()
          .includes(query) ||
        o.vehicle.plate?.toLowerCase().includes(query),
    )
  }

  res.json(orders)
})

router.get('/repairOrders/:id', (req, res) => {
  const order = getRepairOrderById(req.params.id)
  if (!order) {
    return res.status(HTTP_STATUS.NOT_FOUND).json({ error: 'Repair order not found' })
  }
  res.json(order)
})

router.post('/repairOrders', validate(createRepairOrderSchema), (req, res) => {
  try {
    const order = createRepairOrder(req.body)
    res.status(HTTP_STATUS.CREATED).json(order)
  } catch (error) {
    console.error('Error creating repair order:', error)
    const errorMessage =
      error instanceof Error ? error.message : 'Failed to create repair order'
    res.status(HTTP_STATUS.INTERNAL_SERVER_ERROR).json({
      error: 'Failed to create repair order',
      message: errorMessage,
    })
  }
})

router.patch('/repairOrders/:id', validate(updateRepairOrderSchema), (req, res) => {
  const order = getRepairOrderById(req.params.id)
  if (!order) {
    return res.status(HTTP_STATUS.NOT_FOUND).json({ error: 'Repair order not found' })
  }

  // Validate status transition if status is being changed
  if (req.body.status && req.body.status !== order.status) {
    const validation = canTransition(order.status, req.body.status, {
      ...order,
      ...req.body,
    })

    if (!validation.allowed) {
      return res.status(HTTP_STATUS.CONFLICT).json({
        error: ERROR_CODES.INVALID_TRANSITION,
        message: validation.reason,
        from: order.status,
        to: req.body.status,
        allowed: ALLOWED_TRANSITIONS[order.status] || [],
      })
    }
  }

  const updated = updateRepairOrder(req.params.id, req.body)
  if (!updated) {
    return res
      .status(HTTP_STATUS.INTERNAL_SERVER_ERROR)
      .json({ error: 'Failed to update repair order' })
  }

  res.json(updated)
})

router.delete('/repairOrders/:id', (req, res) => {
  const success = deleteRepairOrder(req.params.id)
  if (!success) {
    return res.status(HTTP_STATUS.NOT_FOUND).json({ error: 'Repair order not found' })
  }
  res.status(HTTP_STATUS.NO_CONTENT).send()
})

export default router
