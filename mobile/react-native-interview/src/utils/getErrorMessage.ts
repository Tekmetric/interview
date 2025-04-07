import { ApiError } from '@/api/fetchClient';

export function getErrorMessage(error: unknown): string {
  if (error instanceof ApiError) {
    return error.response?.message || `${error.status}: ${error.statusText}`;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return 'Something went wrong. Please try again.';
}
