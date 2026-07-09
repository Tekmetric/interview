interface QuantityInputProps {
  id: string;
  label: string;
  value: number;
  min?: number;
  className?: string;
  onChange: (quantity: number) => void;
}

export function QuantityInput({
  id,
  label,
  value,
  min = 1,
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
        value={value}
        onChange={(event) => onChange(Number(event.target.value))}
        className={`${className} rounded border border-neutral-300 px-2 py-1 text-sm`}
      />
    </>
  );
}
