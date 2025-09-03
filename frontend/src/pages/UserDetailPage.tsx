import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';

import { FormField, LoadingSpinner } from '../components';
import { useToastContext } from '../contexts/ToastContext';
import { useUser, useUserForm } from '../hooks/useUserQueries';
import { UserFormData, validateField, validateUserForm } from '../utils/validation';

interface UserDetailPageProps {
  editMode?: boolean;
  newUser?: boolean;
}

export const UserDetailPage: React.FC<UserDetailPageProps> = ({
  editMode = false,
  newUser = false,
}) => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const { addSuccessToast, addErrorToast } = useToastContext();
  const [editing, setEditing] = useState(editMode || newUser);

  // Form state
  const [formData, setFormData] = useState<UserFormData>({
    name: '',
    email: '',
    phone: '',
    company: '',
    status: 'Active',
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [touched, setTouched] = useState<Record<string, boolean>>({});

  // React Query hooks
  const { data: user, isLoading, error: fetchError } = useUser(newUser ? undefined : id);

  const { submitForm, isLoading: isSubmitting } = useUserForm(
    id,
    newUser,
    addSuccessToast,
    addErrorToast
  );

  // Get route state if available
  const routeState = location.state as {
    record?: Partial<UserFormData & { id: string }>;
    returnTo?: string;
  } | null;

  // Update editing state when editMode prop changes
  useEffect(() => {
    setEditing(editMode || newUser);
  }, [editMode, newUser]);

  // Initialize form data when user data is loaded or for new user
  useEffect(() => {
    if (newUser) {
      // For new users, keep the initial empty form
      return;
    }

    if (user) {
      setFormData({
        name: user.name || '',
        email: user.email || '',
        phone: user.phone || '',
        company: user.company || '',
        status: (user.status as 'Active' | 'Inactive') || 'Active',
      });
    } else if (routeState?.record && routeState.record.id === id) {
      // Use data from route state if available
      const record = routeState.record;
      setFormData({
        name: record.name || '',
        email: record.email || '',
        phone: record.phone || '',
        company: record.company || '',
        status: (record.status as 'Active' | 'Inactive') || 'Active',
      });
    }
  }, [user, routeState, id, newUser]);

  // Handle field changes with real-time validation
  const handleFieldChange = (field: keyof UserFormData, value: string) => {
    setFormData(prev => ({ ...prev, [field]: value }));

    // Clear the error for this field when user starts typing
    if (fieldErrors[field]) {
      setFieldErrors(prev => ({ ...prev, [field]: '' }));
    }
  };

  // Handle field blur for validation
  const handleFieldBlur = (field: keyof UserFormData) => {
    setTouched(prev => ({ ...prev, [field]: true }));

    const error = validateField(field, formData[field]);
    setFieldErrors(prev => ({ ...prev, [field]: error }));
  };

  // Handle form submission
  const handleSave = async () => {
    // Validate entire form
    const validation = validateUserForm(formData);

    if (!validation.isValid) {
      setFieldErrors(validation.errors);
      // Mark all fields as touched to show errors
      setTouched({
        name: true,
        email: true,
        phone: true,
        company: true,
        status: true,
      });
      return;
    }

    const result = await submitForm(formData);

    // Navigate on success (this will only run if submitForm succeeds)
    if (routeState?.returnTo) {
      navigate(routeState.returnTo);
    } else if (newUser && result) {
      navigate(`/users/${result.id}`);
    } else {
      setEditing(false);
    }
  };

  const handleCancel = () => {
    if (routeState?.returnTo) {
      navigate(routeState.returnTo);
    } else {
      navigate('/users');
    }
  };

  // Loading state
  if (isLoading && !newUser) {
    return <LoadingSpinner />;
  }

  // Error state
  if (fetchError) {
    return (
      <div className='py-12 text-center'>
        <h2 className='mb-4 text-2xl font-bold status-error'>Error</h2>
        <p className='mb-4 text-lg text-gray-800 dark:text-gray-200'>{fetchError.message}</p>
        <div className='space-x-4'>
          <button onClick={() => navigate('/users')} className='btn-primary'>
            Back to Users
          </button>
          <button onClick={() => window.location.reload()} className='btn-secondary'>
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className='mx-auto max-w-4xl space-y-6'>
      {/* Page Header */}
      <div className='flex items-start justify-between'>
        <div>
          <h1 className='text-3xl font-bold text-gray-900 dark:text-white'>
            {newUser ? 'Create New User' : editing ? 'Edit User' : 'User Details'}
          </h1>
          <p className='mt-1 text-gray-600 dark:text-gray-400'>
            {newUser
              ? 'Add a new user to the system'
              : editing
                ? 'Modify user information'
                : `Viewing details for ${user?.name || formData.name}`}
          </p>
        </div>

        <div className='flex space-x-2'>
          {!editing ? (
            <>
              <Link
                to={`/users/${id}/edit`}
                state={{
                  record: user,
                  returnTo: `/users/${id}`,
                }}
                className='btn-primary'
              >
                Edit User
              </Link>
              <button onClick={handleCancel} className='btn-secondary'>
                Back to Users
              </button>
            </>
          ) : (
            <>
              <button
                onClick={handleSave}
                disabled={isSubmitting}
                className='btn-primary disabled:opacity-50'
              >
                {isSubmitting ? 'Saving...' : newUser ? 'Create User' : 'Save Changes'}
              </button>
              <button
                onClick={handleCancel}
                disabled={isSubmitting}
                className='btn-secondary disabled:opacity-50'
              >
                Cancel
              </button>
            </>
          )}
        </div>
      </div>

      {/* User Details/Edit Form */}
      <div className='rounded-lg bg-white p-6 shadow dark:bg-gray-800'>
        <div className='grid gap-6 md:grid-cols-2'>
          <FormField
            label='Name'
            type='text'
            value={formData.name}
            onChange={value => handleFieldChange('name', value)}
            onBlur={() => handleFieldBlur('name')}
            error={touched.name ? fieldErrors.name : ''}
            placeholder={newUser ? "Enter user's full name" : ''}
            required
            disabled={!editing}
          />

          <FormField
            label='Email'
            type='email'
            value={formData.email}
            onChange={value => handleFieldChange('email', value)}
            onBlur={() => handleFieldBlur('email')}
            error={touched.email ? fieldErrors.email : ''}
            placeholder={newUser ? "Enter user's email address" : ''}
            required
            disabled={!editing}
          />

          <FormField
            label='Phone'
            type='tel'
            value={formData.phone || ''}
            onChange={value => handleFieldChange('phone', value)}
            onBlur={() => handleFieldBlur('phone')}
            error={touched.phone ? fieldErrors.phone : ''}
            placeholder={newUser ? 'Enter phone number (optional)' : ''}
            disabled={!editing}
          />

          <FormField
            label='Status'
            type='select'
            value={formData.status}
            onChange={value => handleFieldChange('status', value as 'Active' | 'Inactive')}
            onBlur={() => handleFieldBlur('status')}
            error={touched.status ? fieldErrors.status : ''}
            required
            disabled={!editing}
            options={[
              { value: 'Active', label: 'Active' },
              { value: 'Inactive', label: 'Inactive' },
            ]}
          />

          <FormField
            label='Company'
            type='text'
            value={formData.company || ''}
            onChange={value => handleFieldChange('company', value)}
            onBlur={() => handleFieldBlur('company')}
            error={touched.company ? fieldErrors.company : ''}
            placeholder={newUser ? 'Enter company name (optional)' : ''}
            disabled={!editing}
          />
        </div>

        {!newUser && (
          <div className='mt-6 border-t border-gray-200 pt-6 dark:border-gray-700'>
            <label className='mb-1 block text-sm font-medium text-gray-700 dark:text-gray-300'>
              Created At
            </label>
            <p className='text-gray-900 dark:text-white'>{user?.createdAt || 'N/A'}</p>
          </div>
        )}
      </div>
    </div>
  );
};
