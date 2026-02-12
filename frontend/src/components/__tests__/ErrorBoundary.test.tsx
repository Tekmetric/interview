import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import { ErrorBoundary } from '../error-boundary'

// Component that throws an error
function ThrowError({ shouldThrow }: { shouldThrow: boolean }) {
  if (shouldThrow) {
    throw new Error('Test error')
  }
  return <div>No error</div>
}

describe('ErrorBoundary', () => {
  it('should render children when no error occurs', () => {
    render(
      <ErrorBoundary fallback={() => <div>Error occurred</div>}>
        <div>Child component</div>
      </ErrorBoundary>,
    )

    expect(screen.getByText('Child component')).toBeInTheDocument()
    expect(screen.queryByText('Error occurred')).not.toBeInTheDocument()
  })

  it('should render fallback when error is thrown', () => {
    // Suppress console.error for this test
    const originalError = console.error
    console.error = () => {}

    render(
      <ErrorBoundary fallback={(error) => <div>Error: {error.message}</div>}>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>,
    )

    expect(screen.getByText('Error: Test error')).toBeInTheDocument()
    expect(screen.queryByText('No error')).not.toBeInTheDocument()

    console.error = originalError
  })

  it('should pass error to fallback function', () => {
    // Suppress console.error for this test
    const originalError = console.error
    console.error = () => {}

    render(
      <ErrorBoundary
        fallback={(error) => (
          <div>
            <h1>Something went wrong</h1>
            <p>{error.message}</p>
          </div>
        )}
      >
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>,
    )

    expect(screen.getByText('Something went wrong')).toBeInTheDocument()
    expect(screen.getByText('Test error')).toBeInTheDocument()

    console.error = originalError
  })

  it('should render different children without error', () => {
    render(
      <ErrorBoundary fallback={() => <div>Error occurred</div>}>
        <div>First child</div>
        <div>Second child</div>
      </ErrorBoundary>,
    )

    expect(screen.getByText('First child')).toBeInTheDocument()
    expect(screen.getByText('Second child')).toBeInTheDocument()
    expect(screen.queryByText('Error occurred')).not.toBeInTheDocument()
  })

  it('should update state when error is caught', () => {
    // Suppress console.error for this test
    const originalError = console.error
    console.error = () => {}

    const { rerender } = render(
      <ErrorBoundary fallback={(error) => <div>Error: {error.message}</div>}>
        <ThrowError shouldThrow={false} />
      </ErrorBoundary>,
    )

    expect(screen.getByText('No error')).toBeInTheDocument()

    rerender(
      <ErrorBoundary fallback={(error) => <div>Error: {error.message}</div>}>
        <ThrowError shouldThrow={true} />
      </ErrorBoundary>,
    )

    expect(screen.getByText('Error: Test error')).toBeInTheDocument()

    console.error = originalError
  })
})
