// Shared lifecycle states for the async data hooks, so every consumer checks the
// same `status` field against the same values.
export const STATUS = {
  idle: 'idle',
  loading: 'loading',
  success: 'success',
  error: 'error',
};
