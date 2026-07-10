export class ApiError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'ApiError';
  }
}

export class HttpError extends ApiError {
  readonly status: number;

  constructor(status: number, message?: string) {
    super(message ?? `HTTP error: ${status}`);
    this.name = 'HttpError';
    this.status = status;
  }
}

export class TimeoutError extends ApiError {
  constructor(message = 'Request timed out') {
    super(message);
    this.name = 'TimeoutError';
  }
}

export class NetworkError extends ApiError {
  constructor(message = 'Network error') {
    super(message);
    this.name = 'NetworkError';
  }
}

export class InvalidResponseError extends ApiError {
  constructor(message = 'Invalid response') {
    super(message);
    this.name = 'InvalidResponseError';
  }
}
