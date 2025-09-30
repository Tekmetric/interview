import { useEffect, useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card, CardHeader, CardTitle, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'

function App() {
  const [health, setHealth] = useState<any>(null)
  const [orders, setOrders] = useState<any[]>([])

  useEffect(() => {
    // Test API connection
    fetch('/api/health')
      .then(res => res.json())
      .then(data => setHealth(data))
      .catch(err => console.error('API error:', err))

    // Fetch repair orders
    fetch('/api/repairOrders')
      .then(res => res.json())
      .then(data => setOrders(data.slice(0, 5)))
      .catch(err => console.error('API error:', err))
  }, [])

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="max-w-6xl mx-auto space-y-6">
        <header className="text-center space-y-2">
          <h1 className="text-4xl font-bold text-gray-900">TekBoard ✅</h1>
          <p className="text-lg text-gray-600">
            Modern Kanban board for auto repair shop workflow management
          </p>
          <Badge variant="outline" className="text-green-600 border-green-600">
            Setup Complete
          </Badge>
        </header>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                🎨 Tailwind CSS v4
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="flex gap-2">
                <Badge className="bg-blue-500">NEW</Badge>
                <Badge className="bg-amber-500">AWAITING</Badge>
                <Badge className="bg-indigo-500">IN_PROGRESS</Badge>
              </div>
              <p className="text-sm text-gray-600">
                Using new @tailwindcss/vite plugin with CSS variables
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                🧩 shadcn/ui Components
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <Button>Click me</Button>
              <p className="text-sm text-gray-600">
                Button, Card, Badge, Dialog, Input, Select, Skeleton installed
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                🚀 Backend API
                {health && <Badge variant="outline" className="text-green-600">Online</Badge>}
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              {health ? (
                <div className="text-sm">
                  <p className="font-mono text-green-600">✓ Express server running</p>
                  <p className="font-mono text-green-600">✓ SQLite database connected</p>
                  <p className="font-mono text-green-600">✓ {orders.length > 0 ? `${orders.length}+ repair orders` : 'Loading...'}</p>
                </div>
              ) : (
                <p className="text-sm text-amber-600">Connecting to API...</p>
              )}
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                📦 Dependencies Installed
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-2 gap-2 text-xs">
                <Badge variant="outline">React 19</Badge>
                <Badge variant="outline">TypeScript</Badge>
                <Badge variant="outline">Vite 7</Badge>
                <Badge variant="outline">TanStack Query</Badge>
                <Badge variant="outline">@dnd-kit</Badge>
                <Badge variant="outline">Zod</Badge>
                <Badge variant="outline">wouter</Badge>
                <Badge variant="outline">sonner</Badge>
              </div>
            </CardContent>
          </Card>
        </div>

        {orders.length > 0 && (
          <Card>
            <CardHeader>
              <CardTitle>Sample Repair Orders (First 5)</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                {orders.map(order => (
                  <div key={order.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                    <div>
                      <p className="font-mono text-sm font-semibold">{order.id}</p>
                      <p className="text-sm text-gray-600">
                        {order.vehicle.year} {order.vehicle.make} {order.vehicle.model}
                      </p>
                      <p className="text-xs text-gray-500">{order.customer.name}</p>
                    </div>
                    <Badge className={
                      order.status === 'NEW' ? 'bg-blue-500' :
                      order.status === 'AWAITING_APPROVAL' ? 'bg-amber-500' :
                      order.status === 'IN_PROGRESS' ? 'bg-indigo-500' :
                      order.status === 'WAITING_PARTS' ? 'bg-orange-500' :
                      'bg-green-500'
                    }>
                      {order.status}
                    </Badge>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}

        <div className="text-center text-sm text-gray-500 space-y-1">
          <p>✅ Vite + React 19 + TypeScript + Tailwind v4 + shadcn/ui</p>
          <p>✅ Express + SQLite + Zod validation</p>
          <p>✅ TanStack Query + @dnd-kit + react-hook-form</p>
          <p className="font-semibold text-gray-700 mt-4">Ready to build the Kanban board!</p>
        </div>
      </div>
    </div>
  )
}

export default App
