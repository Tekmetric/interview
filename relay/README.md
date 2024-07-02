# Relay Server
Relay server protects API key from leaking to frontend users. It proxies all requests to coinmarketcap,
appending the API key to the header.

To use the server, create `.env` file in the same directory and run the server via `npm start`.

Sample `.env` file contents below:

```
CMC_API_KEY=my-coin-market-cap-key
FRONTEND_URL=http://localhost:3000
```

`FRONTEND_URL` must match the host and port of the frontend, otherwise the server will refuse access.
