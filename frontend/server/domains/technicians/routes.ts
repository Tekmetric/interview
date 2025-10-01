import express from 'express'
import { getAllTechnicians } from './repository.js'

const router = express.Router()

// Get all technicians
router.get('/technicians', (_req, res) => {
  const technicians = getAllTechnicians()
  res.json(technicians)
})

export default router
