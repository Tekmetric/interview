import { useState } from "react";
import { Link } from "react-router";
import { useAuth } from "../hooks/useAuth";

export default function AuthNavigation() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { isAuthenticated, user, logout } = useAuth();

  const handleLogout = () => {
    logout();
    setIsMenuOpen(false);
  };

  if (isAuthenticated && user) {
    return (
      <>
        <div className="hidden md:flex items-center space-x-4">
          <span className="text-sm text-gray-600 dark:text-gray-300">
            Welcome, {user.name}
          </span>
          <button
            onClick={handleLogout}
            className="text-gray-600 hover:text-gray-900 dark:text-gray-300 dark:hover:text-white px-3 py-2 text-sm font-medium transition-colors"
          >
            Log Out
          </button>
        </div>

        <div className="md:hidden">
          <button
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            className="text-gray-600 hover:text-gray-900 dark:text-gray-300 dark:hover:text-white p-2 transition-colors"
            aria-label="Toggle menu"
          >
            <svg
              className={`w-6 h-6 transition-transform duration-300 ${isMenuOpen ? "rotate-180" : ""}`}
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              {isMenuOpen ? (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M6 18L18 6M6 6l12 12"
                />
              ) : (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
                />
              )}
            </svg>
          </button>
        </div>

        <div
          className={`md:hidden absolute top-full left-0 right-0 bg-white dark:bg-gray-900 border-t border-gray-200 dark:border-gray-700 transition-all duration-200 ease-in-out overflow-hidden ${
            isMenuOpen ? "max-h-96 opacity-100" : "max-h-0 opacity-0"
          }`}
        >
          <div className="py-4">
            <div className="flex flex-col space-y-2">
              <div className="text-right px-3 py-2 text-sm text-gray-600 dark:text-gray-300">
                Welcome, {user.name}
              </div>
              <button
                onClick={handleLogout}
                className="text-right text-gray-600 hover:text-gray-900 dark:text-gray-300 dark:hover:text-white px-3 py-2 text-sm font-medium rounded-md hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
              >
                Log Out
              </button>
            </div>
          </div>
        </div>
      </>
    );
  }

  return (
    <>
      <div className="hidden md:flex items-center space-x-4">
        <Link
          to="/auth/login"
          className="text-gray-600 hover:text-gray-900 dark:text-gray-300 dark:hover:text-white px-3 py-2 text-sm font-medium transition-colors"
        >
          Log In
        </Link>
        <Link
          to="/auth/register"
          className="bg-gray-900 dark:bg-gray-100 text-white dark:text-gray-900 hover:bg-gray-800 dark:hover:bg-gray-200 px-4 py-2 rounded-md text-sm font-medium transition-colors"
        >
          Register
        </Link>
      </div>

      <div className="md:hidden">
        <button
          onClick={() => setIsMenuOpen(!isMenuOpen)}
          className="text-gray-600 hover:text-gray-900 dark:text-gray-300 dark:hover:text-white p-2 transition-colors"
          aria-label="Toggle menu"
        >
          <svg
            className={`w-6 h-6 transition-transform duration-300 ${isMenuOpen ? "rotate-180" : ""}`}
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            {isMenuOpen ? (
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M6 18L18 6M6 6l12 12"
              />
            ) : (
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"
              />
            )}
          </svg>
        </button>
      </div>

      <div
        className={`md:hidden absolute top-full left-0 right-0 bg-white dark:bg-gray-900 border-t border-gray-200 dark:border-gray-700 transition-all duration-200 ease-in-out overflow-hidden ${
          isMenuOpen ? "max-h-96 opacity-100" : "max-h-0 opacity-0"
        }`}
      >
        <div className="py-4">
          <div className="flex flex-col space-y-2">
            <Link
              to="/auth/login"
              className="text-right text-gray-600 hover:text-gray-900 dark:text-gray-300 dark:hover:text-white px-3 py-2 text-sm font-medium rounded-md hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
              onClick={() => setIsMenuOpen(false)}
            >
              Log In
            </Link>
            <Link
              to="/auth/register"
              className="text-right text-gray-600 hover:text-gray-900 dark:text-gray-300 dark:hover:text-white px-3 py-2 text-sm font-medium rounded-md hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors"
              onClick={() => setIsMenuOpen(false)}
            >
              Register
            </Link>
          </div>
        </div>
      </div>
    </>
  );
}
