// Created with the help of AI.
export class ParksApiError extends Error {
  status: number;
  name: string;

  constructor(message: string, status: number) {
    super(message); // Call the parent Error constructor with the message
    this.name = 'ParksApiError'; // Set the name of the custom error

    // Add custom properties specific to ParksApiError
    this.status = status;

    // Capture the stack trace (optional, for better debugging in some environments)
    if (Error.captureStackTrace) {
      Error.captureStackTrace(this, ParksApiError);
    }
  }
}

declare module '@tanstack/react-query' {
  interface Register {
    defaultError: ParksApiError
  }
}