import { Link } from 'react-router-dom';
import { Layout } from '../components/layout';

export const NotFoundPage = () => {
  return (
    <Layout>
      <div className="flex flex-col items-center justify-center py-12">
        <div className="text-center">
          <h1 className="text-6xl font-bold text-gray-800 mb-4">404</h1>
          <h2 className="text-2xl font-semibold text-gray-600 mb-6">Page Not Found</h2>
          <p className="text-gray-500 mb-8">
            The page you are looking for doesn't exist or has been moved.
          </p>
          <Link
            to="/"
            className="px-6 py-3 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
          >
            Go Home
          </Link>
        </div>
      </div>
    </Layout>
  );
};
