import beerLogo from "/beer.svg";

export function BrandLogo() {
  return (
    <div className="flex items-center justify-center">
      <img src={beerLogo} className="h-24 drop-shadow" alt="Brewfinder logo" />
    </div>
  );
}


