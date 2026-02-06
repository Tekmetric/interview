import { BookmarkIcon } from '@/components/ui/icons/akar-icons-bookmark.tsx'

export type LayoutPrimaryProps = {
  title: string;
  children?: React.ReactNode;
};

export const LayoutPrimary = ({ title, children }: LayoutPrimaryProps) => {
  return (
    <>
      <header className="flex items-center gap-4 p-4 sticky top-0 z-30 bg-white shadow-md mb-2 sm:mb-4" role="banner">
        <BookmarkIcon />
        <h1 className="text-2xl font-semibold text-gray-900">{title}</h1>
      </header>

      <main role="main" className="max-w-screen-xl m-auto p-2 sm:p-4 h-full">{children}</main>
    </>
  )
}

