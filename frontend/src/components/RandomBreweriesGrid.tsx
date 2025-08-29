import type { Brewery } from "@/api/breweries";
import { useRandomBreweries } from "@/hooks/useRandomBreweries";
import { BreweryCard } from "@/components/BreweryCard";

export function RandomBreweriesGrid({
  onOpen,
}: {
  onOpen: (id: string) => void;
}) {
  const { data } = useRandomBreweries(4);
  const breweries: Brewery[] = data ?? [];
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      {breweries.map((brewery) => (
        <BreweryCard key={brewery.id} brewery={brewery} onOpen={onOpen} />
      ))}
    </div>
  );
}
