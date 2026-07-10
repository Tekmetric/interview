export default function KeyValueItem({ label, value }: { label: string; value: string | number }) {
    return (
        <div className="flex items-center justify-between md:p-4 p-2">
            <span className="font-semibold">{label}</span>
            <span>{value}</span>
        </div>
    );
}