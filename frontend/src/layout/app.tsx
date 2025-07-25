import { Outlet, Link } from "react-router";
import AuthNavigation from "../components/AuthNavigation";
import ThemeToggle from "../components/ThemeToggle";

export default function AppLayout() {
  return (
    <>
      <header className="bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-700 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16 relative">
            <div className="flex-1"></div>
            
            <Link to="/" className="flex items-center">
              <h1 className="text-xl font-bold text-gray-900 dark:text-white">
                The index of coffee
              </h1>
            </Link>
            
            <div className="flex-1 flex justify-end">
              <AuthNavigation />
            </div>
          </div>
        </div>
      </header>

      <main className="min-h-screen bg-gray-50 dark:bg-gray-900">
        <Outlet />
      </main>

      <ThemeToggle />
    </>
  );
}
