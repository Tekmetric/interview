interface QuantityInputProps {
  id: string;
  label: string;
  value: number;
  min?: number;
  max?: number;
  className?: string;
  onChange: (quantity: number) => void;
}

function sanitizeQuantity(
  rawValue: string,
  min: number,
  max?: number
): number | null {
  const parsed = Number(rawValue);

  if (!Number.isFinite(parsed)) {
    return null;
  }

  let quantity = Math.round(parsed);
  quantity = Math.max(min, quantity);

  if (max !== undefined) {
    quantity = Math.min(max, quantity);
  }

  return quantity;
}

export function QuantityInput({
  id,
  label,
  value,
  min = 1,
  max,
  className = 'w-16',
  onChange,
}: QuantityInputProps) {
  return (
    <>
      <label className="sr-only" htmlFor={id}>
        {label}
      </label>
      <input
        id={id}
        type="number"
        min={min}
        max={max}
        value={value}
        onChange={(event) => {
          const quantity = sanitizeQuantity(event.target.value, min, max);

          if (quantity !== null) {
            onChange(quantity);
          }
        }}
        className={`${className} rounded border border-neutral-300 px-2 py-1 text-sm`}
      />
    </>
  );
}
