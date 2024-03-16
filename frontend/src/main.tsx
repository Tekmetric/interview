import React from 'react'
import { createRoot } from 'react-dom/client'
import { SWRConfig } from 'swr'
import App from './App'
import GlobalStyles from './styles/GlobalStyles'
import { keepStateWhileLoading } from './utils/swr_middleware'

const container = document.getElementById('root')
const root = createRoot(container!)
root.render(
  <React.StrictMode>
    <GlobalStyles />
    <SWRConfig value={{ use: [keepStateWhileLoading] }}>
      <App />
    </SWRConfig>
  </React.StrictMode>,
)
