import type { Brewery } from "@/api/breweries";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";

type Props = {
  brewery: Brewery;
  onOpen?: (id: string) => void;
};

export function BreweryCard({ brewery, onOpen }: Props) {
  const addressParts = [
    brewery.street,
    brewery.city,
    brewery.state,
    brewery.postal_code,
    brewery.country,
  ]
    .filter(Boolean)
    .join(", ");

  return (
    <Card
      className="h-full flex flex-col cursor-pointer overflow-hidden"
      onClick={() => onOpen?.(brewery.id)}
    >
      <CardHeader>
        <CardTitle className="line-clamp-1">{brewery.name}</CardTitle>
        <CardDescription className="flex items-center gap-2 min-w-0">
          <Badge variant="secondary" className="capitalize shrink-0">
            {brewery.brewery_type}
          </Badge>
          {brewery.city && (
            <span className="truncate">
              {brewery.city}
              {brewery.state ? `, ${brewery.state}` : ""}
            </span>
          )}
        </CardDescription>
      </CardHeader>
      <CardContent className="grid gap-2 text-sm">
        {addressParts && (
          <div className="text-muted-foreground line-clamp-2 break-words">
            {addressParts}
          </div>
        )}
        {brewery.phone && (
          <div className="truncate">
            <span className="text-muted-foreground">Phone:</span>{" "}
            {brewery.phone}
          </div>
        )}
        {brewery.website_url && (
          <a
            href={brewery.website_url}
            target="_blank"
            className="text-primary underline underline-offset-4 truncate"
          >
            {brewery.website_url}
          </a>
        )}
      </CardContent>
      <CardFooter className="mt-auto">
        <div className="text-xs text-muted-foreground truncate">
          ID: {brewery.id}
        </div>
      </CardFooter>
    </Card>
  );
}
