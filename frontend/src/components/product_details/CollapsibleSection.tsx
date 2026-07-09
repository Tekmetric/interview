import { useId, useState, type ReactNode } from 'react';
import './productDetails.css';

interface CollapsibleSectionProps {
  title: string;
  children: ReactNode;
  defaultOpen?: boolean;
}

export function CollapsibleSection({
  title,
  children,
  defaultOpen = false,
}: CollapsibleSectionProps) {
  const [isOpen, setIsOpen] = useState(defaultOpen);
  const panelId = useId();

  return (
    <section className="collapsible-section">
      <button
        type="button"
        className="collapsible-section__trigger"
        aria-expanded={isOpen}
        aria-controls={panelId}
        onClick={() => setIsOpen((open) => !open)}
      >
        <span>{title}</span>
        <span className="collapsible-section__icon" aria-hidden="true">
          {isOpen ? '−' : '+'}
        </span>
      </button>
      <div
        id={panelId}
        className="collapsible-section__panel"
        hidden={!isOpen}
      >
        {children}
      </div>
    </section>
  );
}
