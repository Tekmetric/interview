import { Component, ReactNode } from 'react'

import { ErrorMessage } from '../ErrorMessage/ErrorMessage'

type Props = {
  children: ReactNode
  isInverted?: boolean
}

type State = {
  hasError: boolean
}

export class ErrorBoundary extends Component<Props, State> {
  readonly state = { hasError: false }

  componentDidCatch() {
    this.setState({ hasError: true })
  }

  render() {
    if (this.state.hasError) {
      return this.renderError()
    }

    return this.props.children
  }

  renderError() {
    return <ErrorMessage />
  }
}
