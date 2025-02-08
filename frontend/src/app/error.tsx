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
    console.error('Global error:', error)
  }, [error])

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-900">
      <div className="bg-gray-800 p-8 rounded-lg shadow-xl text-center">
        <h2 className="text-2xl font-bold text-red-500 mb-4">
          Something went wrong!
        </h2>
        <p className="text-gray-300 mb-4">
          We apologize for the inconvenience. Our team has been notified and is
          working on the issue.
        </p>
        <p className="text-gray-400 mb-4">
          Error: {error.message || 'An unexpected error occurred'}
        </p>
        {error.digest && (
          <p className="text-gray-400 mb-4">Error ID: {error.digest}</p>
        )}
        <button
          onClick={() => reset()}
          className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded transition duration-300"
        >
          Try again
        </button>
      </div>
    </div>
  )
}
