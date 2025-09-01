import { beforeEach, describe, expect, it, vi } from 'vitest';

import { TableData } from '../types';
import { ApiService } from './ApiService';

// Mock fetch globally
const mockFetch = vi.fn();
global.fetch = mockFetch;

describe('ApiService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockFetch.mockClear();
  });

  describe('fetchUsers', () => {
    const mockApiUsers = [
      {
        id: '1',
        name: 'John Doe',
        email: 'john@example.com',
        phone: '+1234567890',
        company: 'Acme Corp',
        status: 'Active',
        createdAt: '2024-01-15T10:30:00Z',
      },
      {
        id: '2',
        name: 'Jane Smith',
        email: 'jane@example.com',
        phone: '+0987654321',
        company: 'Tech Inc',
        status: 'Inactive',
        createdAt: '2024-01-20T14:45:00Z',
      },
    ];

    it('fetches users successfully', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => mockApiUsers,
      });

      const result = await ApiService.fetchUsers();

      expect(mockFetch).toHaveBeenCalledWith(
        'https://68a8ab89b115e67576e98576.mockapi.io/api/v1/users'
      );
      expect(result).toEqual([
        {
          id: '1',
          name: 'John Doe',
          email: 'john@example.com',
          phone: '+1234567890',
          company: 'Acme Corp',
          status: 'Active',
          createdAt: '2024-01-15T10:30:00Z',
        },
        {
          id: '2',
          name: 'Jane Smith',
          email: 'jane@example.com',
          phone: '+0987654321',
          company: 'Tech Inc',
          status: 'Inactive',
          createdAt: '2024-01-20T14:45:00Z',
        },
      ]);
    });

    it('handles network errors', async () => {
      mockFetch.mockRejectedValueOnce(new Error('Network error'));

      await expect(ApiService.fetchUsers()).rejects.toThrow();
    });

    it('handles HTTP errors', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error',
      });

      await expect(ApiService.fetchUsers()).rejects.toThrow();
    });

    it('handles invalid JSON response', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => {
          throw new Error('Invalid JSON');
        },
      });

      await expect(ApiService.fetchUsers()).rejects.toThrow();
    });
  });

  describe('getUser', () => {
    const mockUser = {
      id: '1',
      name: 'John Doe',
      email: 'john@example.com',
      phone: '+1234567890',
      company: 'Acme Corp',
      status: 'Active',
      createdAt: '2024-01-15T10:30:00Z',
    };

    it('fetches single user successfully', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => mockUser,
      });

      const result = await ApiService.getUser('1');

      expect(mockFetch).toHaveBeenCalledWith(
        'https://68a8ab89b115e67576e98576.mockapi.io/api/v1/users/1'
      );
      expect(result).toEqual({
        id: '1',
        name: 'John Doe',
        email: 'john@example.com',
        phone: '+1234567890',
        company: 'Acme Corp',
        status: 'Active',
        createdAt: '2024-01-15T10:30:00Z',
      });
    });

    it('handles user not found', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 404,
        statusText: 'Not Found',
      });

      await expect(ApiService.getUser('999')).rejects.toThrow();
    });
  });

  describe('createUser', () => {
    const newUserData: Omit<TableData, 'id' | 'createdAt'> = {
      name: 'New User',
      email: 'new@example.com',
      phone: '+1111111111',
      company: 'New Corp',
      status: 'Active',
    };

    const createdUser = {
      id: '3',
      name: 'New User',
      email: 'new@example.com',
      phone: '+1111111111',
      company: 'New Corp',
      status: 'Active',
      createdAt: '2024-01-25T09:00:00Z',
    };

    it('creates user successfully', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 201,
        json: async () => createdUser,
      });

      const result = await ApiService.createUser(newUserData);

      expect(mockFetch).toHaveBeenCalledWith(
        'https://68a8ab89b115e67576e98576.mockapi.io/api/v1/users',
        expect.objectContaining({
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: expect.stringContaining('"name":"New User"'),
        })
      );
      expect(result).toEqual({
        id: '3',
        name: 'New User',
        email: 'new@example.com',
        phone: '+1111111111',
        company: 'New Corp',
        status: 'Active',
        createdAt: '2024-01-25T09:00:00Z',
      });
    });

    it('handles validation errors', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 400,
        statusText: 'Bad Request',
      });

      await expect(ApiService.createUser(newUserData)).rejects.toThrow();
    });

    it('handles server errors during creation', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error',
      });

      await expect(ApiService.createUser(newUserData)).rejects.toThrow();
    });
  });

  describe('updateUser', () => {
    const updateData: Partial<TableData> = {
      name: 'Updated Name',
      status: 'Inactive',
    };

    const updatedUser = {
      id: '1',
      name: 'Updated Name',
      email: 'john@example.com',
      phone: '+1234567890',
      company: 'Acme Corp',
      status: 'Inactive',
      createdAt: '2024-01-15T10:30:00Z',
    };

    it('updates user successfully', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => updatedUser,
      });

      const result = await ApiService.updateUser('1', updateData);

      expect(mockFetch).toHaveBeenCalledWith(
        'https://68a8ab89b115e67576e98576.mockapi.io/api/v1/users/1',
        expect.objectContaining({
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(updateData),
        })
      );
      expect(result).toEqual({
        id: '1',
        name: 'Updated Name',
        email: 'john@example.com',
        phone: '+1234567890',
        company: 'Acme Corp',
        status: 'Inactive',
        createdAt: '2024-01-15T10:30:00Z',
      });
    });

    it('handles user not found during update', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 404,
        statusText: 'Not Found',
      });

      await expect(ApiService.updateUser('999', updateData)).rejects.toThrow();
    });
  });

  describe('deleteUser', () => {
    it('deletes user successfully', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: true,
        status: 200,
        json: async () => ({ success: true }),
      });

      await expect(ApiService.deleteUser('1')).resolves.not.toThrow();

      expect(mockFetch).toHaveBeenCalledWith(
        'https://68a8ab89b115e67576e98576.mockapi.io/api/v1/users/1',
        expect.objectContaining({
          method: 'DELETE',
        })
      );
    });

    it('handles user not found during deletion', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 404,
        statusText: 'Not Found',
      });

      await expect(ApiService.deleteUser('999')).rejects.toThrow();
    });

    it('handles server errors during deletion', async () => {
      mockFetch.mockResolvedValueOnce({
        ok: false,
        status: 500,
        statusText: 'Internal Server Error',
      });

      await expect(ApiService.deleteUser('1')).rejects.toThrow();
    });
  });

  describe('error handling', () => {
    it('handles different HTTP status codes appropriately', async () => {
      const testCases = [
        { status: 400, expectedError: /Invalid data provided/ },
        { status: 401, expectedError: /Authentication required/ },
        { status: 403, expectedError: /Permission denied/ },
        { status: 404, expectedError: /User not found/ },
        { status: 429, expectedError: /Too many requests/ },
        { status: 500, expectedError: /Server error/ },
        { status: 503, expectedError: /Service temporarily unavailable/ },
      ];

      for (const testCase of testCases) {
        mockFetch.mockResolvedValueOnce({
          ok: false,
          status: testCase.status,
          statusText: `Status ${testCase.status}`,
        });

        await expect(ApiService.fetchUsers()).rejects.toThrow(testCase.expectedError);
        mockFetch.mockClear();
      }
    });

    it('handles network connectivity issues', async () => {
      mockFetch.mockRejectedValueOnce(new Error('Failed to fetch'));

      await expect(ApiService.fetchUsers()).rejects.toThrow(/Network connection failed/);
    });

    it('handles timeout errors', async () => {
      mockFetch.mockRejectedValueOnce(new Error('The operation was aborted'));

      await expect(ApiService.fetchUsers()).rejects.toThrow();
    });
  });
});
