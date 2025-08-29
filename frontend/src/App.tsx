import { useEffect, useRef, useState, Suspense, useDeferredValue } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
 
import { useDebounce } from "@uidotdev/usehooks";
import { useQueryClient } from "@tanstack/react-query";
import { ThemeToggle } from "@/components/ThemeToggle";
import { reverseGeocodeCity } from "@/api/geocode";
import { Navigation, Loader2 } from "lucide-react";
import { BreweryDialog } from "@/components/BreweryDialog";
import { BrandLogo } from "@/components/BrandLogo";
import { HeroHeading } from "@/components/HeroHeading";
import { RandomBreweriesSection } from "@/components/RandomBreweriesSection";
import { AutocompleteList } from "@/components/AutocompleteList";

function App() {
  const [query, setQuery] = useState("");
  const debouncedQuery = useDebounce(query, 300);
  const deferredQuery = useDeferredValue(debouncedQuery);

  const queryClient = useQueryClient();

  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const openDialog = (id: string) => {
    setSelectedId(id);
    setDialogOpen(true);
  };
  const [autocompleteOpen, setAutocompleteOpen] = useState(false);
  const [hasFocus, setHasFocus] = useState(false);
  const [locating, setLocating] = useState(false);

  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (hasFocus && debouncedQuery.trim().length >= 2) setAutocompleteOpen(true);
    else if (!hasFocus) setAutocompleteOpen(false);
  }, [hasFocus, debouncedQuery]);

  return (
    <div className="mx-auto max-w-6xl p-8 space-y-10">
      <div className="flex justify-end">
        <ThemeToggle />
      </div>
      <BrandLogo />
      <HeroHeading />

      <div className="max-w-2xl mx-auto relative">
        <Label htmlFor="search" className="sr-only">
          Search breweries
        </Label>
        <div className="flex flex-col sm:flex-row gap-2">
          <Input
            id="search"
            className="w-full"
            placeholder="Search breweries by name, city, or state"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            ref={inputRef}
            onFocus={() => setHasFocus(true)}
            onBlur={() =>
              setTimeout(() => {
                setHasFocus(false);
                setAutocompleteOpen(false);
              }, 100)
            }
            onKeyDown={(event) => {
              if (event.key === "Escape") {
                setAutocompleteOpen(false);
                (event.target as HTMLInputElement).blur();
              }
            }}
          />
          <Button
            type="button"
            onClick={() => {
              inputRef.current?.focus();
              if (query.trim().length >= 2) {
                setAutocompleteOpen(true);
                queryClient.invalidateQueries({ queryKey: ["autocomplete", debouncedQuery, 8] });
              }
            }}
          >
            Search
          </Button>
          <Button
            type="button"
            variant="secondary"
            className="w-[180px]"
            onClick={() => {
              if (!navigator.geolocation) return;
              setLocating(true);
              navigator.geolocation.getCurrentPosition(
                async (pos) => {
                  const { latitude, longitude } = pos.coords;
                  try {
                    const city = await reverseGeocodeCity(latitude, longitude);
                    const pick =
                      city?.city ||
                      city?.locality ||
                      city?.principalSubdivision ||
                      city?.countryName;
                    if (pick) {
                      setQuery(pick);
                      inputRef.current?.focus();
                      if (pick.trim().length >= 2) {
                        setAutocompleteOpen(true);
                        queryClient.invalidateQueries({ queryKey: ["autocomplete", pick, 8] });
                      }
                    }
                  } finally {
                    setLocating(false);
                  }
                },
                () => {
                  setLocating(false);
                }
              );
            }}
            title="Use my location"
            disabled={locating}
          >
            {locating ? (
              <>
                <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                Locating...
              </>
            ) : (
              <>
                <Navigation className="h-4 w-4 mr-2" />
                Use my location
              </>
            )}
          </Button>
        </div>
        {autocompleteOpen && (
          <Suspense fallback={<Card className="p-3 text-sm text-muted-foreground">Loading…</Card>}>
            <AutocompleteList
              query={deferredQuery}
              onPick={(id, name) => {
                setQuery(name);
                setAutocompleteOpen(false);
                openDialog(id);
              }}
            />
          </Suspense>
        )}
      </div>

      <Suspense
        fallback={
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {Array.from({ length: 4 }, (_, index) => index).map((index) => (
              <Card key={index} className="p-6">
                <div className="space-y-3">
                  <Skeleton className="h-6 w-2/3" />
                  <Skeleton className="h-4 w-1/2" />
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-4 w-2/3" />
                </div>
              </Card>
            ))}
          </div>
        }
      >
        <RandomBreweriesSection onOpen={openDialog} />
      </Suspense>

      <BreweryDialog
        id={selectedId}
        open={dialogOpen}
        onOpenChange={setDialogOpen}
      />
    </div>
  );
}

export default App;
