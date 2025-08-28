import { useQuery } from "@tanstack/react-query";
import { fetchBreweryById, type Brewery } from "@/api/breweries";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Badge } from "@/components/ui/badge";
import * as DialogPrimitive from "@radix-ui/react-dialog";
import { Button } from "@/components/ui/button";
import { X, MapPin, Phone, Globe, Crosshair, ExternalLink, Hash, Loader2 } from "lucide-react";

type Props = {
  id: string | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function BreweryDialog({ id, open, onOpenChange }: Props) {
  const { data, isLoading } = useQuery({
    queryKey: ["brewery", id],
    queryFn: () => (id ? fetchBreweryById(id) : Promise.resolve(null)),
    enabled: !!id && open,
    staleTime: 1000 * 60 * 10,
  });
  const b: Brewery | null = data ?? null;

  const address = [b?.street, b?.city, b?.state, b?.postal_code, b?.country].filter(Boolean).join(", ");

  const mapsHref = b
    ? (b.latitude && b.longitude
        ? `https://www.google.com/maps?q=${encodeURIComponent(`${b.latitude},${b.longitude}`)}`
        : address
          ? `https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(address)}`
          : undefined)
    : undefined;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:h-[400px]">
        <DialogPrimitive.Close asChild>
          <Button
            variant="ghost"
            size="icon"
            className="absolute right-4 top-4"
            aria-label="Close"
            title="Close"
          >
            <X className="h-4 w-4" />
          </Button>
        </DialogPrimitive.Close>
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <span>{b?.name || "Brewery"}</span>
            {b?.brewery_type && <Badge variant="secondary" className="capitalize">{b.brewery_type}</Badge>}
          </DialogTitle>
          <DialogDescription>
            {b?.city && (
              <span>{b.city}{b.state ? `, ${b.state}` : ""}</span>
            )}
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 text-sm overflow-auto pr-1">
          {isLoading && (
            <div className="flex h-40 items-center justify-center">
              <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
            </div>
          )}
          {!isLoading && b && (
            <>
              {address && (
                <div className="rounded-lg border p-4 bg-muted/30">
                  <div className="flex items-start gap-3">
                    <MapPin className="h-5 w-5 text-primary mt-0.5" />
                    <div className="flex-1">
                      <div className="text-base font-semibold">Address</div>
                      <div className="text-sm">{address}</div>
                    </div>
                    {mapsHref && (
                      <a
                        href={mapsHref}
                        target="_blank"
                        className="inline-flex items-center gap-1 rounded-md bg-primary px-3 py-1.5 text-primary-foreground text-xs font-medium hover:bg-primary/90"
                        rel="noreferrer"
                      >
                        Open in Maps <ExternalLink className="h-3.5 w-3.5" />
                      </a>
                    )}
                  </div>
                </div>
              )}

              {b.phone && (
                <div className="flex items-center gap-3">
                  <Phone className="h-4 w-4 text-muted-foreground" />
                  <div>
                    <div className="text-xs text-muted-foreground">Phone</div>
                    <a href={`tel:${b.phone}`} className="text-sm text-primary underline">{b.phone}</a>
                  </div>
                </div>
              )}

              {b.website_url && (
                <div className="flex items-center gap-3">
                  <Globe className="h-4 w-4 text-muted-foreground" />
                  <div>
                    <div className="text-xs text-muted-foreground">Website</div>
                    <a href={b.website_url} target="_blank" rel="noreferrer" className="text-sm text-primary underline">{b.website_url}</a>
                  </div>
                </div>
              )}

              {(b.latitude && b.longitude) && (
                <div className="flex items-center gap-3">
                  <Crosshair className="h-4 w-4 text-muted-foreground" />
                  <div>
                    <div className="text-xs text-muted-foreground">Coordinates</div>
                    <div className="text-sm">{b.latitude}, {b.longitude}</div>
                  </div>
                </div>
              )}

              <div className="flex items-center gap-3">
                <Hash className="h-4 w-4 text-muted-foreground" />
                <div>
                  <div className="text-xs text-muted-foreground">ID</div>
                  <div className="text-sm">{b.id}</div>
                </div>
              </div>
            </>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}


