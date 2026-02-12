import React from 'react'
import ReactDOM from 'react-dom/client'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { Route, Switch } from 'wouter'
import { Toaster } from 'sonner'

import { Dashboard } from '@/pages/Dashboard'
import { Kanban } from '@/pages/Kanban'
import { PreferencesProvider } from '@/contexts/preferences-context'
import '@/index.css'

const queryClient = new QueryClient()

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <PreferencesProvider>
        <Switch>
          <Route path='/' component={Dashboard} />
          <Route path='/kanban' component={Kanban} />
          <Route>404 Not Found</Route>
        </Switch>
        <Toaster />
      </PreferencesProvider>
    </QueryClientProvider>
  </React.StrictMode>,
)
