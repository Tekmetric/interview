import { useAuth0 } from '@auth0/auth0-react';
import { Layout } from '../components/layout';

export const ProfilePage = () => {
  const { user } = useAuth0();

  return (
    <Layout>
      <div className="bg-white p-6 rounded-lg shadow-md">
        <h1 className="text-2xl font-bold mb-4">Profile Page</h1>
        <p className="text-gray-600">TBA - User profile content will be added later</p>
        {user && (
          <div className="mt-4 text-sm text-gray-500">
            <p>Currently logged in as: {user.email}</p>
          </div>
        )}
      </div>
    </Layout>
  );
};
