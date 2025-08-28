import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import { Button } from '@/components/ui/button'

function App() {
  const [count, setCount] = useState(0)

  return (
    <div className="mx-auto max-w-screen-md p-8 text-center space-y-6">
      <div className="flex items-center justify-center gap-6">
        <a href="https://vite.dev" target="_blank">
          <img src={viteLogo} className="h-24 transition hover:drop-shadow-[0_0_2em_#646cffaa]" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank">
          <img src={reactLogo} className="h-24 transition hover:drop-shadow-[0_0_2em_#61dafbaa]" alt="React logo" />
        </a>
      </div>
      <h1 className="text-4xl font-bold">Vite + React + shadcn/ui</h1>
      <div className="space-y-3">
        <Button onClick={() => setCount((count) => count + 1)}>count is {count}</Button>
        <p className="text-muted-foreground">
          Edit <code>src/App.tsx</code> and save to test HMR
        </p>
      </div>
      <p className="text-sm text-muted-foreground">Click on the Vite and React logos to learn more</p>
    </div>
  )
}

export default App
