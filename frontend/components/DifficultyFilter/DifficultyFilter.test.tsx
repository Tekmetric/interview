import { fireEvent, render } from '@testing-library/react'
import React from 'react'

import DifficultyFilter from './DifficultyFilter'

describe('DifficultyFilter', () => {
  it('should render all difficulty options', () => {
    const mockSetSelectedDifficulties = jest.fn()

    const { getByText } = render(
      <DifficultyFilter
        selectedDifficulties={['beginner', 'intermediate']}
        setSelectedDifficulties={mockSetSelectedDifficulties}
      />
    )

    expect(getByText(/Beginner/i)).toBeInTheDocument()
    expect(getByText(/Intermediate/i)).toBeInTheDocument()
    expect(getByText(/Expert/i)).toBeInTheDocument()
  })

  it('should toggle difficulty when clicked', () => {
    const mockSetSelectedDifficulties = jest.fn()

    const { getByText } = render(
      <DifficultyFilter
        selectedDifficulties={['beginner']}
        setSelectedDifficulties={mockSetSelectedDifficulties}
      />
    )

    const beginnerButton = getByText(/Beginner/i)

    fireEvent.click(beginnerButton)

    expect(mockSetSelectedDifficulties).toHaveBeenCalledWith([])
  })
})
