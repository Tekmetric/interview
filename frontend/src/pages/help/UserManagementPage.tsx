import React from 'react';

export const UserManagementPage: React.FC = () => {
  return (
    <div className='space-y-6'>
      <h2 className='text-2xl font-bold text-gray-900 dark:text-white'>User Management</h2>

      <div className='prose dark:prose-invert max-w-none'>
        <p className='text-lg text-gray-600 dark:text-gray-300'>
          Learn how to manage users in this dashboard application.
        </p>

        <h3 className='mb-4 mt-8 text-xl font-semibold text-gray-900 dark:text-white'>Overview</h3>
        <p className='text-gray-600 dark:text-gray-300'>
          The user management system allows you to view, search, and manage user records. All data
          is displayed in a sortable table with pagination support.
        </p>

        <h3 className='mb-4 mt-8 text-xl font-semibold text-gray-900 dark:text-white'>
          Viewing Users
        </h3>
        <p className='mb-4 text-gray-600 dark:text-gray-300'>
          The Users page displays all users in a table format with the following information:
        </p>
        <ul className='mb-4 list-inside list-disc space-y-1 text-gray-600 dark:text-gray-300'>
          <li>
            <strong>Name:</strong> User&apos;s full name
          </li>
          <li>
            <strong>Email:</strong> Contact email address
          </li>
          <li>
            <strong>Phone:</strong> Phone number (when available)
          </li>
          <li>
            <strong>Company:</strong> Associated company
          </li>
          <li>
            <strong>Status:</strong> Current status (Active/Inactive)
          </li>
          <li>
            <strong>Created:</strong> When the user was added
          </li>
        </ul>

        <h3 className='mb-4 mt-8 text-xl font-semibold text-gray-900 dark:text-white'>
          User Details
        </h3>
        <p className='mb-4 text-gray-600 dark:text-gray-300'>
          Click on the view button in any user&apos;s row to view their detailed profile page with
          complete information and contact details.
        </p>

        <h3 className='mb-4 mt-8 text-xl font-semibold text-gray-900 dark:text-white'>
          Search and Filtering
        </h3>
        <div className='space-y-4'>
          <div>
            <h4 className='mb-2 font-semibold text-gray-900 dark:text-white'>Search Users</h4>
            <p className='text-gray-600 dark:text-gray-300'>
              Use the search bar to find users by name, email, or company. The search is
              case-insensitive and updates results as you type.
            </p>
          </div>

          <div>
            <h4 className='mb-2 font-semibold text-gray-900 dark:text-white'>Filter by Status</h4>
            <p className='text-gray-600 dark:text-gray-300'>
              Use the status dropdown to show only Active or Inactive users. Select &quot;All&quot;
              to see all users regardless of status.
            </p>
          </div>
        </div>

        <h3 className='mb-4 mt-8 text-xl font-semibold text-gray-900 dark:text-white'>
          Sorting and Pagination
        </h3>
        <ul className='list-inside list-disc space-y-2 text-gray-600 dark:text-gray-300'>
          <li>Click on any column header to sort by that field</li>
          <li>Click again to reverse the sort order</li>
          <li>Use pagination controls at the bottom to navigate through pages</li>
          <li>Adjust page size using the dropdown (10, 25, 50, or 100 users per page)</li>
        </ul>

        <div className='my-6 border-l-4 border-blue-400 bg-blue-50 p-4 dark:bg-blue-900/20'>
          <p className='text-sm text-blue-700 dark:text-blue-200'>
            <strong>Navigation Tip:</strong> Each user has a unique URL that you can bookmark or
            share. URLs are updated automatically as you search and navigate.
          </p>
        </div>

        <div className='mt-6 rounded-lg bg-green-50 p-4 dark:bg-green-900/20'>
          <h4 className='mb-2 font-semibold text-green-900 dark:text-green-100'>Quick Tips:</h4>
          <ul className='list-inside list-disc space-y-1 text-green-800 dark:text-green-200'>
            <li>Combine search with status filters for more precise results</li>
            <li>Sort by &quot;Created&quot; to see the newest or oldest users first</li>
            <li>Bookmark search results by copying the URL</li>
          </ul>
        </div>
      </div>
    </div>
  );
};
