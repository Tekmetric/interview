import { useRandomBreweries } from "@/hooks/useRandomBreweries";
import { useQueryClient } from "@tanstack/react-query";
import type { Brewery } from "@/api/breweries";
import { BreweryCard } from "@/components/BreweryCard";
import { Button } from "@/components/ui/button";

export function RandomBreweriesSection({
  onOpen,
}: {
  onOpen: (id: string) => void;
}) {
  const { data, refetch, isFetching } = useRandomBreweries(4);
  const queryClient = useQueryClient();
  const breweries: Brewery[] = data ?? [];
  return (
    <>
      <div className="flex items-center gap-2 mt-2">
        <Button
          variant="ghost"
          type="button"
          onClick={async () => {
            // Remove cached data so Suspense fallback (skeleton) shows during refetch
            queryClient.removeQueries({ queryKey: ["random-breweries", 4] });
            await refetch();
          }}
          disabled={isFetching}
        >
          {isFetching ? "Refreshing…" : "Refresh random"}
        </Button>
        <span className="text-sm text-muted-foreground">
          Showing 4 random breweries
        </span>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {breweries.map((brewery) => (
          <BreweryCard key={brewery.id} brewery={brewery} onOpen={onOpen} />
        ))}
      </div>
    </>
  );
}
