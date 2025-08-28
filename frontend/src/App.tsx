import { useMemo, useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { useQuery } from '@tanstack/react-query'
import { fetchRandomBreweries } from '@/api/breweries'
import type { Brewery } from '@/api/breweries'
import { BreweryCard } from '@/components/BreweryCard'

function App() {
  const [query, setQuery] = useState("")
  const { data, isLoading, isError, refetch, isFetching } = useQuery({
    queryKey: ["random-breweries"],
    queryFn: () => fetchRandomBreweries(4),
    staleTime: 1000 * 60 * 5,
  })
  const breweries: Brewery[] = useMemo(() => data ?? [], [data])

  return (
    <div className="mx-auto max-w-6xl p-8 space-y-10">
      <div className="flex items-center justify-center gap-6">
        <a href="https://vite.dev" target="_blank">
          <img src={viteLogo} className="h-24 transition hover:drop-shadow-[0_0_2em_#646cffaa]" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank">
          <img src={reactLogo} className="h-24 transition hover:drop-shadow-[0_0_2em_#61dafbaa]" alt="React logo" />
        </a>
      </div>
      <div className="text-center space-y-3">
        <h1 className="text-4xl md:text-5xl font-bold tracking-tight">Find your next brewery</h1>
        <p className="text-muted-foreground max-w-2xl mx-auto">
          Explore breweries, cideries, and brewpubs across the world with data from the Open Brewery DB.
        </p>
      </div>

      <div className="max-w-2xl mx-auto">
        <Label htmlFor="search" className="sr-only">Search breweries</Label>
        <div className="flex gap-2">
          <Input id="search" placeholder="Search breweries by name, city, or state" value={query} onChange={(e) => setQuery(e.target.value)} />
          <Button type="button" onClick={() => {/* will wire search in next step */}}>
            Search
          </Button>
        </div>
        <div className="flex items-center gap-2 mt-2">
          <Button variant="ghost" type="button" onClick={() => refetch()} disabled={isFetching}>
            {isFetching ? 'Refreshing…' : 'Refresh random' }
          </Button>
          <span className="text-sm text-muted-foreground">Showing 4 random breweries</span>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {isLoading && Array.from({ length: 4 }).map((_, i) => (
          <Card key={i} className="p-6">
            <div className="space-y-3">
              <Skeleton className="h-6 w-2/3" />
              <Skeleton className="h-4 w-1/2" />
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-2/3" />
            </div>
          </Card>
        ))}

        {isError && (
          <div className="col-span-full text-center text-destructive">Failed to load breweries. Try again.</div>
        )}

        {!isLoading && !isError && breweries.map((b) => (
          <BreweryCard key={b.id} brewery={b} />
        ))}
      </div>
    </div>
  )
}

export default App
