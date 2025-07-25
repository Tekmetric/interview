import { useParams, Link } from "react-router";
import { useCoffee } from "../../hooks/useCoffeeQueries";
import { useAuth } from "../../hooks/useAuth";

export default function CoffeePage() {
  const { id } = useParams();
  const { data: coffee, isLoading, error } = useCoffee(id!);
  const { isAuthenticated, user } = useAuth();

  if (isLoading) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="flex justify-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-900 dark:border-gray-100"></div>
        </div>
      </div>
    );
  }

  if (error || !coffee) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <p className="text-red-600 dark:text-red-400">
            Error: {error?.message || "Coffee not found"}
          </p>
          <Link 
            to="/" 
            className="mt-4 inline-block bg-gray-900 dark:bg-gray-100 text-white dark:text-gray-900 px-4 py-2 rounded-md hover:bg-gray-800 dark:hover:bg-gray-200 transition-colors"
          >
            Back to Coffee List
          </Link>
        </div>
      </div>
    );
  }

  const isOwner = isAuthenticated && user?.id === coffee.userId;

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div className="flex items-center justify-between mb-8">
        <Link 
          to="/" 
          className="inline-flex items-center text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white text-sm font-medium"
        >
          <svg
            className="w-4 h-4 mr-2"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M15 19l-7-7 7-7"
            />
          </svg>
          Back to Coffee Collection
        </Link>

        <div className="flex items-center space-x-3">
          {isAuthenticated && (
            <Link
              to="/coffee/new"
              className="inline-flex items-center px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md shadow-sm text-sm font-medium text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
            >
              <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
              </svg>
              Add New Coffee
            </Link>
          )}

          {isOwner && (
            <Link
              to={`/coffee/edit/${coffee.id}`}
              className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-gray-900 dark:bg-gray-100 dark:text-gray-900 hover:bg-gray-800 dark:hover:bg-gray-200 transition-colors"
            >
              <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
              </svg>
              Edit Coffee
            </Link>
          )}
        </div>
      </div>

      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-lg dark:shadow-gray-900/20 overflow-hidden">
        <div className="p-8 md:p-12">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12">
            <div className="flex items-center justify-center bg-gray-100 dark:bg-gray-700 rounded-lg min-h-96">
              <div className="text-center text-gray-500 dark:text-gray-400">
                <svg
                  className="w-16 h-16 mx-auto mb-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={1}
                    d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                  />
                </svg>
                <p>Coffee Image</p>
              </div>
            </div>

            <div>
              <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-4">
                {coffee.farmName}
              </h1>
              <p className="text-4xl font-bold text-green-600 dark:text-green-400 mb-6">
                {coffee.price}
              </p>
              
              <div className="space-y-6">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                    Details
                  </h3>
                  <p className="text-gray-600 dark:text-gray-300">
                    <span className="font-medium">Altitude:</span>{" "}
                    {coffee.altitude}m above sea level
                  </p>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-3">
                    Taste Notes
                  </h3>
                  <div className="flex flex-wrap gap-2">
                    {coffee.tasteNotes.map((note, index) => (
                      <span
                        key={index}
                        className="bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 px-3 py-1 rounded-full text-sm font-medium"
                      >
                        {note}
                      </span>
                    ))}
                  </div>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-3">
                    Description
                  </h3>
                  <p className="text-gray-600 dark:text-gray-300 leading-relaxed">
                    {coffee.description}
                  </p>
                </div>

                {!isOwner && (
                  <div className="pt-6">
                    <button className="w-full bg-gray-900 dark:bg-gray-100 text-white dark:text-gray-900 py-3 px-6 rounded-md hover:bg-gray-800 dark:hover:bg-gray-200 transition-colors font-medium">
                      Add to Cart
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
