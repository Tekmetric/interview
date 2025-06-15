import { useAuth0 } from '@auth0/auth0-react';
import { Layout } from '../components/layout';

export const UserDetailsPage = () => {
  const { user } = useAuth0();

  return (
    <Layout>
      <div className="bg-white p-6 rounded-lg shadow-md max-w-3xl mx-auto">
        <h1 className="text-2xl font-bold mb-6 text-gray-800 border-b pb-2">User Details</h1>

        {user ? (
          <div className="space-y-6">
            <div className="flex items-center space-x-4">
              {user.picture && (
                <img
                  src={user.picture}
                  alt="Profile"
                  className="h-20 w-20 rounded-full border-2 border-blue-500"
                />
              )}
              <div>
                <h2 className="text-xl font-semibold">{user.name}</h2>
                <p className="text-gray-600">{user.email}</p>
              </div>
            </div>

            <div className="border rounded-lg p-4 bg-gray-50 max-w-2xl mx-auto">
              <h3 className="font-medium text-gray-700 mb-2">User Information</h3>
              <ul className="space-y-2">
                <li className="flex">
                  <span className="text-gray-500 w-32">Email Verified:</span>
                  <span className="font-medium">{user.email_verified ? 'Yes' : 'No'}</span>
                </li>
                <li className="flex">
                  <span className="text-gray-500 w-32">Nickname:</span>
                  <span className="font-medium">{user.nickname || 'N/A'}</span>
                </li>
                <li className="flex">
                  <span className="text-gray-500 w-32">User ID:</span>
                  <span className="font-medium">{user.sub}</span>
                </li>
                <li className="flex">
                  <span className="text-gray-500 w-32">Updated At:</span>
                  <span className="font-medium">
                    {user.updated_at ? new Date(user.updated_at).toLocaleString() : 'N/A'}
                  </span>
                </li>
              </ul>
            </div>
          </div>
        ) : (
          <div className="text-center py-8">
            <p className="text-gray-600">No user information available. Please log in.</p>
          </div>
        )}
      </div>
    </Layout>
  );
};
