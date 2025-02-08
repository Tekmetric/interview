import { useState, useEffect } from 'react'

const DEFAULT_DATE = '2022-07-21T01:15:00Z'

export function useCustomDate(initialDate: string = DEFAULT_DATE): Date {
  const [currentDate, setCurrentDate] = useState(new Date(initialDate))

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentDate((prevDate) => new Date(prevDate.getTime() + 1000))
    }, 1000)

    return () => clearInterval(timer)
  }, [])

  return currentDate
}
