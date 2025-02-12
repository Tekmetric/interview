import { setupServer } from 'msw/node';
import { handlers } from './tests/msw';
import '@testing-library/jest-dom';

export const server = setupServer(...handlers);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());
