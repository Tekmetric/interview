import { render, screen } from '@testing-library/react'
import '@testing-library/jest-dom'
import { CountdownDisplay } from '../CountdownDisplay'

describe('CountdownDisplay', () => {
  it('renders countdown values correctly', () => {
    const timeLeft = {
      days: 1,
      hours: 2,
      minutes: 3,
      seconds: 4,
    }

    render(<CountdownDisplay timeLeft={timeLeft} />)

    expect(screen.getByText('01')).toBeInTheDocument()
    expect(screen.getByText('02')).toBeInTheDocument()
    expect(screen.getByText('03')).toBeInTheDocument()
    expect(screen.getByText('04')).toBeInTheDocument()

    expect(screen.getByText('days')).toBeInTheDocument()
    expect(screen.getByText('hours')).toBeInTheDocument()
    expect(screen.getByText('minutes')).toBeInTheDocument()
    expect(screen.getByText('seconds')).toBeInTheDocument()
  })
})
