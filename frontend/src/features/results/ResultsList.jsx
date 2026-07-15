import ArtworkRow from './ArtworkRow';

export default function ResultsList({ items, onSelect, showImageLink = false }) {
  return (
    <ul className="space-y-2">
      {items.map((artwork, index) => (
        <ArtworkRow
          key={artwork.id}
          artwork={artwork}
          index={index}
          onSelect={onSelect}
          showImageLink={showImageLink}
        />
      ))}
    </ul>
  );
}
