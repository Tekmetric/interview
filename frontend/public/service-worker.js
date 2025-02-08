// The service worker is used for offline capabilities and improved performance
// by caching essential assets and intercepting network requests.
const CACHE_NAME = 'spacex-dashboard-cache-v1'

// Utility function to add a timestamp header to responses for cache expiration.
function addTimestamp(response) {
  const clonedHeaders = new Headers(response.headers)
  clonedHeaders.set('sw-cache-timestamp', Date.now().toString())
  return response.blob().then((blob) => {
    return new Response(blob, {
      status: response.status,
      statusText: response.statusText,
      headers: clonedHeaders,
    })
  })
}

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches
      .open(CACHE_NAME)
      .then((cache) => {
        return cache
          .add('/')
          .catch((err) => console.error('Failed to cache /:', err))
      })
      .catch((err) => console.error('Failed to open cache:', err))
  )
})

self.addEventListener('fetch', (event) => {
  // Ignore non-HTTP/HTTPS requests
  if (!event.request.url.startsWith('http')) {
    return
  }

  event.respondWith(
    caches.match(event.request).then((cachedResponse) => {
      if (cachedResponse) {
        const timestamp = cachedResponse.headers.get('sw-cache-timestamp')
        // Consider the cache stale if it's missing a timestamp or is older than 30 minutes.
        const isStale =
          !timestamp || Date.now() - parseInt(timestamp, 10) > 30 * 60 * 1000
        if (!isStale) {
          return cachedResponse
        } else {
          // Remove stale cache entry
          caches.open(CACHE_NAME).then((cache) => {
            cache.delete(event.request)
          })
        }
      }

      // If no cache or the cache is stale, fetch from network
      const fetchRequest = event.request.clone()
      return fetch(fetchRequest).then((response) => {
        // Check if we received a valid response.
        if (!response || response.status !== 200 || response.type !== 'basic') {
          return response
        }

        // Only caching GET requests.
        if (event.request.method === 'GET') {
          const responseToCache = response.clone()

          // Add timestamp header and then cache the response.
          addTimestamp(responseToCache).then((responseWithTimestamp) => {
            caches.open(CACHE_NAME).then((cache) => {
              cache
                .put(event.request, responseWithTimestamp)
                .catch((err) => console.error('Failed to cache response:', err))
            })
          })
        }

        return response
      })
    })
  )
})

self.addEventListener('activate', (event) => {
  const cacheWhitelist = [CACHE_NAME]
  event.waitUntil(
    caches.keys().then((cacheNames) => {
      return Promise.all(
        cacheNames.map((cacheName) => {
          if (cacheWhitelist.indexOf(cacheName) === -1) {
            return caches.delete(cacheName)
          }
        })
      )
    })
  )
})
