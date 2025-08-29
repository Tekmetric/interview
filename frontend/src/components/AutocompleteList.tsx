import { Card } from "@/components/ui/card";
import { useBreweriesAutocomplete } from "@/hooks/useBreweriesAutocomplete";
import type { BreweryAutocomplete } from "@/api/breweries";
import { SearchX } from "lucide-react";

export function AutocompleteList({
  query,
  onPick,
}: {
  query: string;
  onPick: (id: string, name: string) => void;
}) {
  const { data } = useBreweriesAutocomplete(query, 8);
  const suggestions: BreweryAutocomplete[] = data ?? [];
  return (
    <div className="absolute mt-2 w-full z-10">
      <Card className="p-2 divide-y max-h-[60vh] overflow-y-auto">
        {suggestions.length === 0 ? (
          <div className="flex items-center gap-3 p-4 text-sm text-muted-foreground">
            <SearchX className="h-5 w-5" />
            <div>
              <div>No breweries found.</div>
              <div>
              Try searching for something like <span className="font-medium text-foreground">"New York"</span>.
              </div>
            </div>
          </div>
        ) : (
          suggestions.map((suggestion) => (
            <button
              key={suggestion.id}
              className="w-full text-left px-3 py-2 hover:bg-accent rounded-md"
              onClick={() => onPick(suggestion.id, suggestion.name)}
            >
              <div className="font-medium truncate">{suggestion.name}</div>
              {(suggestion.city || suggestion.state) && (
                <div className="text-xs text-muted-foreground truncate">
                  {[suggestion.city, suggestion.state]
                    .filter(Boolean)
                    .join(", ")}
                </div>
              )}
            </button>
          ))
        )}
      </Card>
    </div>
  );
}
