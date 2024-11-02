import { type RenderResult, render, screen } from '@testing-library/react'

import { Page } from './page'

const renderComponent = (): RenderResult => render(<Page>hello</Page>)

describe('page', () => {
  it('should render the children', () => {
    renderComponent()

    expect(screen.getByText('hello')).toBeInTheDocument()
  })
})
