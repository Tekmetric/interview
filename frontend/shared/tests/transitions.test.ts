import { describe, it, expect } from 'vitest'
import { canTransition, ALLOWED_TRANSITIONS } from '../transitions'
import type { RepairOrder } from '../types'

describe('Status Transitions', () => {
  describe('ALLOWED_TRANSITIONS map', () => {
    it('should define allowed transitions for NEW status', () => {
      expect(ALLOWED_TRANSITIONS.NEW).toEqual(['AWAITING_APPROVAL', 'IN_PROGRESS'])
    })

    it('should define allowed transitions for AWAITING_APPROVAL status', () => {
      expect(ALLOWED_TRANSITIONS.AWAITING_APPROVAL).toEqual(['IN_PROGRESS', 'NEW'])
    })

    it('should define allowed transitions for IN_PROGRESS status', () => {
      expect(ALLOWED_TRANSITIONS.IN_PROGRESS).toEqual([
        'WAITING_PARTS',
        'COMPLETED',
        'AWAITING_APPROVAL',
      ])
    })

    it('should define allowed transitions for WAITING_PARTS status', () => {
      expect(ALLOWED_TRANSITIONS.WAITING_PARTS).toEqual(['IN_PROGRESS'])
    })

    it('should define no transitions for COMPLETED status', () => {
      expect(ALLOWED_TRANSITIONS.COMPLETED).toEqual([])
    })
  })

  describe('canTransition - same status', () => {
    it('should allow staying in the same status', () => {
      const result = canTransition('NEW', 'NEW')
      expect(result.allowed).toBe(true)
      expect(result.reason).toBeUndefined()
    })
  })

  describe('canTransition - allowed transitions', () => {
    it('should allow NEW -> AWAITING_APPROVAL', () => {
      const result = canTransition('NEW', 'AWAITING_APPROVAL')
      expect(result.allowed).toBe(true)
    })

    it('should allow NEW -> IN_PROGRESS with assigned tech', () => {
      const order: Partial<RepairOrder> = {
        assignedTech: {
          id: 'tech-1',
          name: 'John',
          initials: 'JD',
          specialties: [],
          active: true,
        },
      }
      const result = canTransition('NEW', 'IN_PROGRESS', order)
      expect(result.allowed).toBe(true)
    })

    it('should allow AWAITING_APPROVAL -> IN_PROGRESS with assigned tech', () => {
      const order: Partial<RepairOrder> = {
        assignedTech: {
          id: 'tech-1',
          name: 'John',
          initials: 'JD',
          specialties: [],
          active: true,
        },
      }
      const result = canTransition('AWAITING_APPROVAL', 'IN_PROGRESS', order)
      expect(result.allowed).toBe(true)
    })

    it('should allow AWAITING_APPROVAL -> NEW', () => {
      const result = canTransition('AWAITING_APPROVAL', 'NEW')
      expect(result.allowed).toBe(true)
    })

    it('should allow IN_PROGRESS -> WAITING_PARTS', () => {
      const result = canTransition('IN_PROGRESS', 'WAITING_PARTS')
      expect(result.allowed).toBe(true)
    })

    it('should allow IN_PROGRESS -> COMPLETED with customer approval', () => {
      const order: Partial<RepairOrder> = {
        approvedByCustomer: true,
      }
      const result = canTransition('IN_PROGRESS', 'COMPLETED', order)
      expect(result.allowed).toBe(true)
    })

    it('should allow IN_PROGRESS -> AWAITING_APPROVAL', () => {
      const result = canTransition('IN_PROGRESS', 'AWAITING_APPROVAL')
      expect(result.allowed).toBe(true)
    })

    it('should allow WAITING_PARTS -> IN_PROGRESS with assigned tech', () => {
      const order: Partial<RepairOrder> = {
        assignedTech: {
          id: 'tech-1',
          name: 'John',
          initials: 'JD',
          specialties: [],
          active: true,
        },
      }
      const result = canTransition('WAITING_PARTS', 'IN_PROGRESS', order)
      expect(result.allowed).toBe(true)
    })
  })

  describe('canTransition - blocked transitions', () => {
    it('should block NEW -> WAITING_PARTS', () => {
      const result = canTransition('NEW', 'WAITING_PARTS')
      expect(result.allowed).toBe(false)
      expect(result.reason).toContain('Cannot move from NEW to WAITING_PARTS')
    })

    it('should block NEW -> COMPLETED', () => {
      const result = canTransition('NEW', 'COMPLETED')
      expect(result.allowed).toBe(false)
      expect(result.reason).toContain('Cannot move from NEW to COMPLETED')
    })

    it('should block AWAITING_APPROVAL -> WAITING_PARTS', () => {
      const result = canTransition('AWAITING_APPROVAL', 'WAITING_PARTS')
      expect(result.allowed).toBe(false)
      expect(result.reason).toContain(
        'Cannot move from AWAITING_APPROVAL to WAITING_PARTS',
      )
    })

    it('should block AWAITING_APPROVAL -> COMPLETED', () => {
      const result = canTransition('AWAITING_APPROVAL', 'COMPLETED')
      expect(result.allowed).toBe(false)
      expect(result.reason).toContain('Cannot move from AWAITING_APPROVAL to COMPLETED')
    })

    it('should block WAITING_PARTS -> NEW', () => {
      const result = canTransition('WAITING_PARTS', 'NEW')
      expect(result.allowed).toBe(false)
      expect(result.reason).toContain('Cannot move from WAITING_PARTS to NEW')
    })

    it('should block WAITING_PARTS -> COMPLETED', () => {
      const result = canTransition('WAITING_PARTS', 'COMPLETED')
      expect(result.allowed).toBe(false)
      expect(result.reason).toContain('Cannot move from WAITING_PARTS to COMPLETED')
    })

    it('should block any transition from COMPLETED', () => {
      const result = canTransition('COMPLETED', 'NEW')
      expect(result.allowed).toBe(false)
      expect(result.reason).toContain('Cannot move from COMPLETED to NEW')
    })
  })

  describe('canTransition - business rules', () => {
    it('should block transition to IN_PROGRESS without assigned tech', () => {
      const order: Partial<RepairOrder> = {
        assignedTech: null,
      }
      const result = canTransition('NEW', 'IN_PROGRESS', order)
      expect(result.allowed).toBe(false)
      expect(result.reason).toBe('Assign a technician before starting work')
    })

    it('should block transition to IN_PROGRESS when tech is undefined', () => {
      const order: Partial<RepairOrder> = {}
      const result = canTransition('AWAITING_APPROVAL', 'IN_PROGRESS', order)
      expect(result.allowed).toBe(false)
      expect(result.reason).toBe('Assign a technician before starting work')
    })

    it('should block transition to COMPLETED without customer approval', () => {
      const order: Partial<RepairOrder> = {
        approvedByCustomer: false,
      }
      const result = canTransition('IN_PROGRESS', 'COMPLETED', order)
      expect(result.allowed).toBe(false)
      expect(result.reason).toBe('Customer approval required before marking as completed')
    })

    it('should block transition to COMPLETED when approval is undefined', () => {
      const order: Partial<RepairOrder> = {}
      const result = canTransition('IN_PROGRESS', 'COMPLETED', order)
      expect(result.allowed).toBe(false)
      expect(result.reason).toBe('Customer approval required before marking as completed')
    })
  })
})
