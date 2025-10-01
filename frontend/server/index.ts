import express from 'express'
import cors from 'cors'
import '@server/data/schema' // Initialize database schema
import techniciansRouter from '@server/domains/technicians/routes'
import repairOrdersRouter from '@server/domains/repair-orders/routes'

const app = express()
const PORT = 3001

app.use(cors())
app.use(express.json())

// Health check
app.get('/api/health', (_req, res) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() })
})

// Mount domain routers
app.use('/api', techniciansRouter)
app.use('/api', repairOrdersRouter)

app.listen(PORT, () => {
  console.log(`🚀 Server running on http://localhost:${PORT}`)
  console.log(`📊 API available at http://localhost:${PORT}/api`)
})
