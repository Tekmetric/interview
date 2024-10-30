'use client' // Error boundaries must be Client Components

const GlobalError = ({
  error: _error,
  reset
}: {
  error: Error & { digest?: string }
  reset: () => void
}): JSX.Element => (
  // global-error must include html and body tags
  <html lang='en'>
    <body>
      <h2>Something went wrong!</h2>

      <button type='button' onClick={reset}>
        Try again
      </button>
    </body>
  </html>
)

export default GlobalError
