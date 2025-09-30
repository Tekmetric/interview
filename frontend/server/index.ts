import express from 'express'
import cors from 'cors'
import {
  getAllRepairOrders,
  getRepairOrderById,
  createRepairOrder,
  updateRepairOrder,
  deleteRepairOrder,
  getAllTechnicians,
} from './db.js'
import { canTransition, ALLOWED_TRANSITIONS } from './transitions.js'

const app = express()
const PORT = 3001

app.use(cors())
app.use(express.json())

// Health check
app.get('/api/health', (_req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() })
})

// Get all technicians
app.get('/api/technicians', (_req, res) => {
  const technicians = getAllTechnicians()
  res.json(technicians)
})

// Get all repair orders with optional filters
app.get('/api/repairOrders', (req, res) => {
  const { status, tech, priority, search } = req.query
  
  let orders = getAllRepairOrders()
  
  // Apply filters
  if (status) {
    orders = orders.filter(o => o.status === status)
  }
  if (tech) {
    orders = orders.filter(o => o.assignedTech?.id === tech)
  }
  if (priority) {
    orders = orders.filter(o => o.priority === priority)
  }
  if (search) {
    const query = (search as string).toLowerCase()
    orders = orders.filter(o =>
      o.id.toLowerCase().includes(query) ||
      o.customer.name.toLowerCase().includes(query) ||
      `${o.vehicle.year} ${o.vehicle.make} ${o.vehicle.model}`.toLowerCase().includes(query) ||
      o.vehicle.plate?.toLowerCase().includes(query)
    )
  }
  
  res.json(orders)
})

// Get single repair order
app.get('/api/repairOrders/:id', (req, res) => {
  const order = getRepairOrderById(req.params.id)
  if (!order) {
    return res.status(404).json({ error: 'Repair order not found' })
  }
  res.json(order)
})

// Create new repair order
app.post('/api/repairOrders', (req, res) => {
  try {
    const order = createRepairOrder(req.body)
    res.status(201).json(order)
  } catch (error) {
    res.status(400).json({ error: 'Invalid repair order data' })
  }
})

// Update repair order (including status transitions)
app.patch('/api/repairOrders/:id', (req, res) => {
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
app.delete('/api/repairOrders/:id', (req, res) => {
  const success = deleteRepairOrder(req.params.id)
  if (!success) {
    return res.status(404).json({ error: 'Repair order not found' })
  }
  res.status(204).send()
})

app.listen(PORT, () => {
  console.log(`🚀 Server running on http://localhost:${PORT}`)
  console.log(`📊 API available at http://localhost:${PORT}/api`)
})
