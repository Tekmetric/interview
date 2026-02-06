import { Component, type ReactNode } from 'react'
import { Button } from '@/components/ui/button.tsx'

interface Props {
  children: ReactNode
}

interface State {
  hasError: boolean
  error: Error | null
}

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props)
    this.state = { hasError: false, error: null }
  }

  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error }
  }

  componentDidCatch(error: Error) {
    // We can send error details to an error reporting service here
    console.error('Error boundary caught an error:', error)
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex items-center justify-center mt-16">
          <div className="text-center">
            <h1 className="text-3xl font-bold text-gray-900 mb-4">Oops! Something went wrong</h1>
            <p className="text-gray-600 mb-2">{this.state.error?.message}</p>
            <Button
              onClick={() => window.location.reload()}
              className="mt-4 transition-colors cursor-pointer"
            >
              Reload Page
            </Button>
          </div>
        </div>
      )
    }

    return this.props.children
  }
}

