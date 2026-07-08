import { CriteriaMenu } from './CriteriaMenu';

export function PageFooter() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="border-t border-neutral-200">
      <div className="max-w-7xl mx-auto px-4 py-4 flex items-center justify-center relative">
        <p className="text-sm text-neutral-500">
          &copy; {currentYear} Productpalooza. All rights reserved.
        </p>
        <div className="absolute right-4">
          <CriteriaMenu />
        </div>
      </div>
    </footer>
  );
}
