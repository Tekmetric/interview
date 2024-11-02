import { type RenderResult, render, screen } from '@testing-library/react'

import { CardsSkeletonLoader } from './cards-skeleton-loader'

const renderComponent = (cards?: number): RenderResult =>
  render(<CardsSkeletonLoader cards={cards} />)

describe('CardsSkeletonLoader', () => {
  it('render the correct number of skeleton cards', () => {
    renderComponent(3)

    expect(screen.getAllByTestId('card-skeleton-loader')).toHaveLength(3)
  })

  it('render default number of skeleton cards when no cards prop is provided', () => {
    renderComponent()

    expect(screen.getAllByTestId('card-skeleton-loader')).toHaveLength(5)
  })
})
