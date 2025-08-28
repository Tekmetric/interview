import { useMemo, useState } from 'react'
import beerLogo from '/beer.svg'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Card } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { useQuery } from '@tanstack/react-query'
import { fetchRandomBreweries, fetchBreweriesAutocomplete, type BreweryAutocomplete } from '@/api/breweries'
import type { Brewery } from '@/api/breweries'
import { BreweryCard } from '@/components/BreweryCard'
import { useDebounce } from '@uidotdev/usehooks'
import { ThemeToggle } from '@/components/ThemeToggle'
import { reverseGeocodeCity } from '@/api/geocode'
import { Navigation } from 'lucide-react'
import { BreweryDialog } from '@/components/BreweryDialog'

function App() {
  const [query, setQuery] = useState("")
  const { data, isLoading, isError, refetch, isFetching } = useQuery({
    queryKey: ["random-breweries"],
    queryFn: () => fetchRandomBreweries(4),
    staleTime: 1000 * 60 * 5,
  })
  const breweries: Brewery[] = useMemo(() => data ?? [], [data])

  const debouncedQuery = useDebounce(query, 100)
  const { data: suggestions } = useQuery({
    queryKey: ["autocomplete", debouncedQuery],
    queryFn: () => fetchBreweriesAutocomplete(debouncedQuery, 8),
    enabled: debouncedQuery.trim().length >= 2,
    staleTime: 1000 * 60 * 10,
  })
  const ac: BreweryAutocomplete[] = suggestions ?? []
  const [selectedId, setSelectedId] = useState<string | null>(null)
  const [dialogOpen, setDialogOpen] = useState(false)
  const openDialog = (id: string) => {
    setSelectedId(id)
    setDialogOpen(true)
  }

  return (
    <div className="mx-auto max-w-6xl p-8 space-y-10">
      <div className="flex justify-end">
        <ThemeToggle />
      </div>
      <div className="flex items-center justify-center">
        <img src={beerLogo} className="h-24 drop-shadow" alt="Brewfinder logo" />
      </div>
      <div className="text-center space-y-3">
        <h1 className="text-4xl md:text-5xl font-bold tracking-tight">Find your next brewery</h1>
        <p className="text-muted-foreground max-w-2xl mx-auto">
          Explore breweries, cideries, and brewpubs across the world with data from the Open Brewery DB.
        </p>
      </div>

      <div className="max-w-2xl mx-auto relative">
        <Label htmlFor="search" className="sr-only">Search breweries</Label>
        <div className="flex gap-2">
          <Input id="search" placeholder="Search breweries by name, city, or state" value={query} onChange={(e) => setQuery(e.target.value)} />
          <Button type="button" onClick={() => {/* will wire search in next step */}}>
            Search
          </Button>
          <Button
            type="button"
            variant="secondary"
            onClick={() => {
              if (!navigator.geolocation) return;
              navigator.geolocation.getCurrentPosition(async (pos) => {
                const { latitude, longitude } = pos.coords;
                const city = await reverseGeocodeCity(latitude, longitude);
                const pick = city?.city || city?.locality || city?.principalSubdivision || city?.countryName;
                if (pick) setQuery(pick);
              });
            }}
            title="Use my location"
          >
            <Navigation className="h-4 w-4 mr-2" />
            Use my location
          </Button>
        </div>
        {ac.length > 0 && (
          <div className="absolute mt-2 w-full z-10">
            <Card className="p-2 divide-y max-h-[60vh] overflow-y-auto">
              {ac.map((s) => (
                <button
                  key={s.id}
                  className="w-full text-left px-3 py-2 hover:bg-accent rounded-md"
                  onClick={() => { setQuery(s.name); openDialog(s.id); }}
                >
                  <div className="font-medium truncate">{s.name}</div>
                  {(s.city || s.state) && (
                    <div className="text-xs text-muted-foreground truncate">{[s.city, s.state].filter(Boolean).join(', ')}</div>
                  )}
                </button>
              ))}
            </Card>
          </div>
        )}
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
          <BreweryCard key={b.id} brewery={b} onOpen={openDialog} />
        ))}
      </div>

      <BreweryDialog id={selectedId} open={dialogOpen} onOpenChange={setDialogOpen} />
    </div>
  )
}

export default App
