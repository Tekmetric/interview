'use client'

import { useEffect } from 'react'

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string }
  reset: () => void
}): React.ReactElement {
  useEffect(() => {
    console.error('API error:', error)
  }, [error])

  return (
    <div
      className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded relative"
      role="alert"
    >
      <strong className="font-bold">API Error!</strong>
      <span className="block sm:inline">
        {' '}
        Something went wrong with the API request.
      </span>
      <p className="mt-2">
        Error: {error.message || 'An unexpected error occurred'}
      </p>
      {error.digest && <p className="mt-2">Error ID: {error.digest}</p>}
      <button
        onClick={() => reset()}
        className="mt-2 bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded"
      >
        Try Again
      </button>
    </div>
  )
}
