// Core data types
export interface TableData {
  id: string;
  name: string;
  email: string;
  status: string;
  createdAt: string;
  phone?: string;
  company?: string;
}

export interface PaginationInfo {
  currentPage: number;
  totalPages: number;
  pageSize: number;
  totalRecords: number;
}

export interface SortConfig {
  key: string;
  direction: 'asc' | 'desc';
}

export interface FilterConfig {
  searchTerm: string;
  statusFilter: string;
}

export interface ApiError {
  operation: 'create' | 'update' | 'delete' | 'view';
  message: string;
  details?: string;
  timestamp: Date;
}

// API response types
export interface ApiUser {
  id: number;
  name: string;
  email: string;
  phone: string;
  company?: {
    name: string;
  };
}

// Theme types
export type Theme = 'light' | 'dark' | 'system';

export interface ThemeContextType {
  theme: Theme;
  actualTheme: 'light' | 'dark';
  setTheme: (theme: Theme) => void;
}

// Error types
export interface ErrorWithMessage {
  message: string;
}

export type UnknownError = Error | ErrorWithMessage | string | unknown;
