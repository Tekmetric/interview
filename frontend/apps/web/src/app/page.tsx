import { Login } from './components/login/login'
import { PageBackgroundClassNames } from './styles'

const Page = (): JSX.Element => (
  <main className={PageBackgroundClassNames}>
    <Login />
  </main>
)

export default Page
