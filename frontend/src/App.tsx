import { Route, Switch } from 'wouter'
import { Welcome } from '@/pages/Welcome'
import { Dashboard } from '@/pages/Dashboard'
import { COMMON_LABELS } from '@shared/constants'

function App() {
  return (
    <Switch>
      <Route path='/' component={Welcome} />
      <Route path='/dashboard' component={Dashboard} />
      <Route>{COMMON_LABELS.NOT_FOUND}</Route>
    </Switch>
  )
}

export default App
