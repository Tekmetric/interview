'use client'

import { motion } from 'framer-motion'
import { memo, useEffect, useReducer, useState } from 'react'

import { LoadingSkeleton } from '@/app/components/ui/loading-skeleton'
import { useCustomDate } from '@/app/hooks/useCustomDate'
import { useNextLaunch } from '@/app/hooks/useSpaceXData'
import type { Launch, Launchpad } from '@/app/types'

import { CountdownDisplay } from './CountdownDisplay'
import { LaunchInfo } from './LaunchInfo'

interface CountdownTimerProps {
  initialData: Launch
}

interface TimeLeft {
  days: number
  hours: number
  minutes: number
  seconds: number
}

type State = {
  launch: Launch | null
  launchpad: Launchpad | null
  error: string | null
}

type Action =
  | { type: 'SET_LAUNCH'; payload: Launch }
  | { type: 'SET_LAUNCHPAD'; payload: Launchpad }
  | { type: 'SET_ERROR'; payload: string }

const initialState: State = {
  launch: null,
  launchpad: null,
  error: null,
}

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case 'SET_LAUNCH':
      return { ...state, launch: action.payload }
    case 'SET_LAUNCHPAD':
      return { ...state, launchpad: action.payload }
    case 'SET_ERROR':
      return { ...state, error: action.payload }
    default:
      return state
  }
}

function CountdownTimer({
  initialData,
}: CountdownTimerProps): React.ReactElement {
  const [state, dispatch] = useReducer(reducer, {
    ...initialState,
    launch: initialData,
  })
  const { data: nextLaunch, launchpad, error } = useNextLaunch(initialData)
  const customDate = useCustomDate()
  const [timeLeft, setTimeLeft] = useState<TimeLeft>({
    days: 0,
    hours: 0,
    minutes: 0,
    seconds: 0,
  })

  useEffect(() => {
    if (nextLaunch) dispatch({ type: 'SET_LAUNCH', payload: nextLaunch })
    if (
      launchpad &&
      typeof launchpad.latitude === 'number' &&
      typeof launchpad.longitude === 'number'
    ) {
      dispatch({ type: 'SET_LAUNCHPAD', payload: launchpad as Launchpad })
    }
    if (error) dispatch({ type: 'SET_ERROR', payload: error.message })
  }, [nextLaunch, launchpad, error])

  useEffect(() => {
    if (!state.launch) return

    const calculateTimeLeft = (): TimeLeft => {
      const now = Math.floor(customDate.getTime() / 1000)
      const difference = state.launch!.date_unix - now

      if (difference <= 0) {
        return { days: 0, hours: 0, minutes: 0, seconds: 0 }
      }

      return {
        days: Math.floor(difference / (60 * 60 * 24)),
        hours: Math.floor((difference % (60 * 60 * 24)) / (60 * 60)),
        minutes: Math.floor((difference % (60 * 60)) / 60),
        seconds: Math.floor(difference % 60),
      }
    }

    const updateTimer = (): void => {
      setTimeLeft(calculateTimeLeft())
    }

    // Initial calculation
    updateTimer()
    const timer = setInterval(updateTimer, 1000)

    return () => clearInterval(timer)
  }, [state.launch, customDate])

  if (state.error) {
    return (
      <div
        className="card p-4 text-center text-destructive"
        data-testid="error-message"
      >
        {state.error}
      </div>
    )
  }

  if (!state.launch) {
    return <LoadingSkeleton className="h-96" data-testid="loading-skeleton" />
  }

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 1 }}
      className="card mb-8"
      data-testid="countdown-timer"
    >
      <h2
        className="section-title text-center mb-6"
        data-testid="countdown-title"
      >
        Next SpaceX Launch
      </h2>
      <CountdownDisplay timeLeft={timeLeft} />
      <LaunchInfo launch={state.launch} launchpad={state.launchpad} />
    </motion.div>
  )
}

export default memo(CountdownTimer)
