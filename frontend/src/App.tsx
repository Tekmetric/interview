import { Route, Switch } from 'wouter'
import { Welcome } from '@/pages/Welcome'
import { Dashboard } from '@/pages/Dashboard'

function App() {
  return (
    <Switch>
      <Route path='/' component={Welcome} />
      <Route path='/dashboard' component={Dashboard} />
      <Route>404 - Not Found</Route>
    </Switch>
  )
}

export default App
