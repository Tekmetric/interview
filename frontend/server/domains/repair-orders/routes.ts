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

const router = Router()

// Get overdue repair orders
router.get('/repairOrders/overdue', (req, res) => {
  const limit = req.query.limit ? parseInt(req.query.limit as string, 10) : 5
  const orders = getOverdueOrders(limit)
  res.json(orders)
})

// Get recent repair orders
router.get('/repairOrders/recent', (req, res) => {
  const limit = req.query.limit ? parseInt(req.query.limit as string, 10) : 5
  const orders = getRecentOrders(limit)
  res.json(orders)
})

// Get all repair orders with optional filters
router.get('/repairOrders', validate(repairOrderFiltersSchema, 'query'), (req, res) => {
  const { status, tech, priority, search } = req.query

  let orders = getAllRepairOrders()

  // Apply filters
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

// Get single repair order
router.get('/repairOrders/:id', (req, res) => {
  const order = getRepairOrderById(req.params.id)
  if (!order) {
    return res.status(404).json({ error: 'Repair order not found' })
  }
  res.json(order)
})

// Create new repair order
router.post('/repairOrders', validate(createRepairOrderSchema), (req, res) => {
  try {
    const order = createRepairOrder(req.body)
    res.status(201).json(order)
  } catch (error) {
    res.status(500).json({ error: 'Failed to create repair order' })
  }
})

// Update repair order (including status transitions)
router.patch('/repairOrders/:id', validate(updateRepairOrderSchema), (req, res) => {
  const order = getRepairOrderById(req.params.id)
  if (!order) {
    return res.status(404).json({ error: 'Repair order not found' })
  }

  // Validate status transition if status is being changed
  if (req.body.status && req.body.status !== order.status) {
    const validation = canTransition(order.status, req.body.status, {
      ...order,
      ...req.body,
    })

    if (!validation.allowed) {
      return res.status(409).json({
        error: 'INVALID_TRANSITION',
        message: validation.reason,
        from: order.status,
        to: req.body.status,
        allowed: ALLOWED_TRANSITIONS[order.status] || [],
      })
    }
  }

  const updated = updateRepairOrder(req.params.id, req.body)
  if (!updated) {
    return res.status(500).json({ error: 'Failed to update repair order' })
  }

  res.json(updated)
})

// Delete repair order
router.delete('/repairOrders/:id', (req, res) => {
  const success = deleteRepairOrder(req.params.id)
  if (!success) {
    return res.status(404).json({ error: 'Repair order not found' })
  }
  res.status(204).send()
})

export default router
