interface QuantityInputProps {
  id: string;
  label: string;
  value: number;
  min?: number;
  max?: number;
  className?: string;
  disabled?: boolean;
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
  disabled = false,
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
        disabled={disabled}
        onChange={(event) => {
          const quantity = sanitizeQuantity(event.target.value, min, max);

          if (quantity !== null) {
            onChange(quantity);
          }
        }}
        className={`${className} min-h-11 rounded border border-neutral-300 px-2 py-1 text-sm focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-600 disabled:cursor-not-allowed disabled:border-neutral-200 disabled:bg-neutral-100 disabled:text-neutral-400`}
      />
    </>
  );
}
