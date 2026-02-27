'use client'

import { Component, type ErrorInfo, type ReactNode } from 'react'

type FallbackProps = {
  error: Error
  reset: () => void
}

type ErrorBoundaryProps = {
  children: ReactNode
  fallback?: (props: FallbackProps) => ReactNode
  onError?: (error: Error, errorInfo: ErrorInfo) => void
}

type ErrorBoundaryState = {
  hasError: boolean
  error: Error | null
}

function DefaultFallback({ error, reset }: FallbackProps) {
  return (
    <div className="mx-auto mt-16 max-w-lg rounded-xl border border-red-200 bg-red-50 p-6 text-red-900 shadow-sm">
      <h2 className="text-lg font-semibold">Something went wrong.</h2>
      <p className="mt-2 text-sm text-red-800">
        The app hit an unexpected error. Try again or reload the page.
      </p>
      {error.message ? (
        <p className="mt-2 rounded border border-red-200 bg-white px-3 py-2 text-xs text-red-700">
          {error.message}
        </p>
      ) : null}
      <button
        type="button"
        onClick={reset}
        className="mt-4 rounded-md bg-red-600 px-4 py-2 text-sm font-medium text-white hover:bg-red-700"
      >
        Try again
      </button>
    </div>
  )
}

export class ErrorBoundary extends Component<ErrorBoundaryProps, ErrorBoundaryState> {
  public state: ErrorBoundaryState = {
    hasError: false,
    error: null,
  }

  public static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return { hasError: true, error }
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo): void {
    if (this.props.onError) {
      this.props.onError(error, errorInfo)
      return
    }

    console.error('Unhandled UI error:', error, errorInfo)
  }

  private reset = () => {
    this.setState({ hasError: false, error: null })
  }

  public render() {
    if (!this.state.hasError || !this.state.error) {
      return this.props.children
    }

    if (this.props.fallback) {
      return this.props.fallback({ error: this.state.error, reset: this.reset })
    }

    return <DefaultFallback error={this.state.error} reset={this.reset} />
  }
}
