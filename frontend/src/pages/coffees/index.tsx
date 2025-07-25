import { useState } from "react";
import { Link } from "react-router";
import { useCoffees } from "../../hooks/useCoffeeQueries";
import { useAuth } from "../../hooks/useAuth";

type ViewMode = "grid" | "list";

export default function CoffeeListPage() {
  const [currentPage, setCurrentPage] = useState(1);
  const [viewMode, setViewMode] = useState<ViewMode>("grid");
  const itemsPerPage = 12;

  const { data, isLoading, error } = useCoffees(currentPage, itemsPerPage);
  const { isAuthenticated } = useAuth();
  
  const coffees = data?.coffees || [];
  const totalCount = data?.totalCount || 0;
  const totalPages = Math.ceil(totalCount / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = Math.min(startIndex + itemsPerPage, totalCount);

  const goToPage = (page: number) => {
    setCurrentPage(page);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const renderPagination = () => {
    if (totalPages <= 1) return null;

    const pages = [];
    const maxVisiblePages = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    const endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }

    return (
      <div className="flex items-center justify-center space-x-2 mt-12">
        <button
          onClick={() => goToPage(currentPage - 1)}
          disabled={currentPage === 1}
          className="px-3 py-2 text-sm font-medium text-gray-500 dark:text-gray-400 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          Previous
        </button>

        {startPage > 1 && (
          <>
            <button
              onClick={() => goToPage(1)}
              className="px-3 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
            >
              1
            </button>
            {startPage > 2 && (
              <span className="px-2 text-gray-500 dark:text-gray-400">...</span>
            )}
          </>
        )}

        {pages.map((page) => (
          <button
            key={page}
            onClick={() => goToPage(page)}
            className={`px-3 py-2 text-sm font-medium rounded-md transition-colors ${
              page === currentPage
                ? "bg-gray-900 dark:bg-gray-100 text-white dark:text-gray-900"
                : "text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 hover:bg-gray-50 dark:hover:bg-gray-700"
            }`}
          >
            {page}
          </button>
        ))}

        {endPage < totalPages && (
          <>
            {endPage < totalPages - 1 && (
              <span className="px-2 text-gray-500 dark:text-gray-400">...</span>
            )}
            <button
              onClick={() => goToPage(totalPages)}
              className="px-3 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
            >
              {totalPages}
            </button>
          </>
        )}

        <button
          onClick={() => goToPage(currentPage + 1)}
          disabled={currentPage === totalPages}
          className="px-3 py-2 text-sm font-medium text-gray-500 dark:text-gray-400 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          Next
        </button>
      </div>
    );
  };

  const renderGridView = () => (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
      {coffees.map((coffee) => (
        <Link
          key={coffee.id}
          to={`/coffee/${coffee.id}`}
          className="bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg dark:shadow-gray-900/20 dark:hover:shadow-gray-900/40 transition-shadow overflow-hidden"
        >
          <div className="p-6">
            <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
              {coffee.farmName}
            </h3>
            <p className="text-2xl font-bold text-green-600 dark:text-green-400 mb-3">
              {coffee.price}
            </p>
            <p className="text-sm text-gray-600 dark:text-gray-300 mb-3">
              Altitude: {coffee.altitude}m
            </p>
            
            <div className="mb-4">
              <p className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Taste Notes:
              </p>
              <div className="flex flex-wrap gap-1">
                {coffee.tasteNotes.slice(0, 3).map((note, index) => (
                  <span
                    key={index}
                    className="inline-block bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 text-xs px-2 py-1 rounded"
                  >
                    {note}
                  </span>
                ))}
                {coffee.tasteNotes.length > 3 && (
                  <span className="inline-block text-gray-500 dark:text-gray-400 text-xs px-2 py-1">
                    +{coffee.tasteNotes.length - 3} more
                  </span>
                )}
              </div>
            </div>
            
            <p className="text-sm text-gray-600 dark:text-gray-300 line-clamp-2">
              {coffee.description}
            </p>
          </div>
        </Link>
      ))}
    </div>
  );

  const renderListView = () => (
    <div className="space-y-4">
      {coffees.map((coffee) => (
        <Link
          key={coffee.id}
          to={`/coffee/${coffee.id}`}
          className="bg-white dark:bg-gray-800 rounded-lg shadow-md hover:shadow-lg dark:shadow-gray-900/20 dark:hover:shadow-gray-900/40 transition-shadow overflow-hidden block"
        >
          <div className="p-6">
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <h3 className="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                  {coffee.farmName}
                </h3>
                <p className="text-sm text-gray-600 dark:text-gray-300 mb-3">
                  Altitude: {coffee.altitude}m above sea level
                </p>
                <p className="text-gray-600 dark:text-gray-300 mb-4 leading-relaxed">
                  {coffee.description}
                </p>
                
                <div className="flex flex-wrap gap-2">
                  {coffee.tasteNotes.map((note, index) => (
                    <span
                      key={index}
                      className="inline-block bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 text-sm px-3 py-1 rounded-full"
                    >
                      {note}
                    </span>
                  ))}
                </div>
              </div>
              
              <div className="ml-6 text-right">
                <p className="text-3xl font-bold text-green-600 dark:text-green-400">
                  {coffee.price}
                </p>
              </div>
            </div>
          </div>
        </Link>
      ))}
    </div>
  );

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="flex justify-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 dark:border-gray-100"></div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <p className="text-red-600 dark:text-red-400">
            Error: {error.message}
          </p>
          <p className="text-gray-500 dark:text-gray-400 mt-2">
            Make sure the JSON server is running on port 3000
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div className="text-center mb-8">
        <h1 className="text-4xl font-bold text-gray-900 dark:text-white mb-4">
          Our Coffee Collection
        </h1>
        <p className="text-lg text-gray-600 dark:text-gray-300">
          Discover premium coffees from around the world
        </p>
        {isAuthenticated && (
          <div className="mt-6">
            <Link
              to="/coffee/new"
              className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-md text-white bg-gray-900 dark:bg-gray-100 dark:text-gray-900 hover:bg-gray-800 dark:hover:bg-gray-200 transition-colors"
            >
              <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
              </svg>
              Add New Coffee
            </Link>
          </div>
        )}
      </div>

      <div className="flex items-center justify-between mb-8">
        <div className="flex items-center text-sm text-gray-600 dark:text-gray-400">
          <span>
            Showing {startIndex + 1}-{endIndex} of {totalCount} coffees
          </span>
        </div>

        <div className="hidden md:flex items-center space-x-2">
          <span className="text-sm text-gray-600 dark:text-gray-400">
            View:
          </span>
          <button
            onClick={() => setViewMode("grid")}
            className={`p-2 rounded-md transition-colors ${
              viewMode === "grid"
                ? "bg-gray-900 dark:bg-gray-100 text-white dark:text-gray-900"
                : "text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800"
            }`}
            aria-label="Grid view"
          >
            <svg
              className="w-5 h-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z"
              />
            </svg>
          </button>
          <button
            onClick={() => setViewMode("list")}
            className={`p-2 rounded-md transition-colors ${
              viewMode === "list"
                ? "bg-gray-900 dark:bg-gray-100 text-white dark:text-gray-900"
                : "text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white hover:bg-gray-100 dark:hover:bg-gray-800"
            }`}
            aria-label="List view"
          >
            <svg
              className="w-5 h-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M4 6h16M4 10h16M4 14h16M4 18h16"
              />
            </svg>
          </button>
        </div>
      </div>

      {viewMode === "grid" ? renderGridView() : renderListView()}
      
      {renderPagination()}
    </div>
  );
}
