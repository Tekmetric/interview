// ============================================================================
// API ENDPOINTS
// ============================================================================
export const API_ENDPOINTS = {
  REPAIR_ORDERS: {
    BASE: '/api/repairOrders',
    BY_ID: (id: string) => `/api/repairOrders/${id}`,
    OVERDUE: '/api/repairOrders/overdue',
    RECENT: '/api/repairOrders/recent',
  },
  TECHNICIANS: {
    BASE: '/api/technicians',
  },
} as const

// ============================================================================
// ENUMS & STATUS VALUES
// ============================================================================
export const RO_STATUS = {
  NEW: 'NEW',
  AWAITING_APPROVAL: 'AWAITING_APPROVAL',
  IN_PROGRESS: 'IN_PROGRESS',
  WAITING_PARTS: 'WAITING_PARTS',
  COMPLETED: 'COMPLETED',
} as const

export const REPAIR_ORDER_STATUSES = Object.values(RO_STATUS)

export const PRIORITY = {
  HIGH: 'HIGH',
  NORMAL: 'NORMAL',
} as const

export const PRIORITIES = Object.values(PRIORITY)

// ============================================================================
// LABELS - Organized by Domain/Feature
// ============================================================================

// Common/Shared Labels
export const COMMON_LABELS = {
  CANCEL: 'Cancel',
  DELETE: 'Delete',
  DELETING: 'Deleting...',
  NOT_FOUND: '404 - Not Found',
  CLEAR_ALL: 'Clear all',
  UNASSIGNED: 'Unassigned',
  SAVING: 'Saving...',
  SAVE_CHANGES: 'Save Changes',
  HIGH_PRIORITY: 'High Priority',
  TECHNICIAN: 'Technician:',
} as const

// Repair Order Labels
export const REPAIR_ORDER_LABELS = {
  // Titles & Headers
  TITLE: 'Repair Order',
  DETAILS: 'Repair Order Details',
  CREATE_NEW: 'Create New Repair Order',
  NEW_ORDER: 'New Order',
  CREATE_NOT_IMPLEMENTED: 'Create RO not yet implemented',
  DELETE_TITLE: 'Delete Repair Order',
  ORDER_INFORMATION: 'Order Information',
  ORDER_ID: 'Order ID',
  CREATED: 'Created',
  LAST_UPDATED: 'Last Updated',
  CUSTOMER: 'Customer',
  VEHICLE: 'Vehicle',
  SERVICES: 'Services',
  UPDATE_ORDER: 'Update Order',
  STATUS: 'Status',
  ASSIGNED_TECHNICIAN: 'Assigned Technician',
  SELECT_TECHNICIAN: 'Select technician',
  PRIORITY: 'Priority',
  APPROVED_BY_CUSTOMER: 'Approved by Customer',
  NOTES: 'Notes',
  ADD_NOTES_PLACEHOLDER: 'Add notes about this repair order...',
  ESTIMATES: 'Estimates',
  DURATION: 'Duration',
  HOURS: 'hours',
  COST: 'Cost',
  DUE: 'Due',
  PLATE: 'Plate',
  MI: 'mi',

  // Messages & Descriptions
  ENTER_DETAILS: 'Enter customer, vehicle, and service details',
  DELETE_CONFIRMATION:
    'Are you sure you want to delete repair order {order.id}? This action cannot be undone.',

  // Success Messages
  CREATED_SUCCESS: 'Repair order created successfully',
  UPDATED_SUCCESS: 'Repair order updated successfully',
  DELETED_SUCCESS: 'Repair order deleted',
  STATUS_UPDATED: 'Order status updated',

  // Error Messages
  FAILED_TO_LOAD: 'Failed to load repair order. Please try again.',
  FAILED_TO_CREATE: 'Failed to create repair order',
  FAILED_TO_UPDATE: 'Failed to update repair order',
  FAILED_TO_DELETE: 'Failed to delete repair order',
  FAILED_TO_FETCH: 'Failed to fetch repair order',
  FAILED_TO_UPDATE_STATUS: 'Failed to update order status',
  ERROR_LOADING_LIST: 'Error loading repair orders. Please try again.',
} as const

// Dashboard Labels
export const DASHBOARD_LABELS = {
  TITLE: 'Dashboard',
  SUBTITLE: 'Quick overview of repair orders',
  TOTAL_WIP: 'Total WIP',
  OVERDUE: 'Overdue',
  WAITING_PARTS: 'Waiting Parts',
  AWAITING_APPROVAL: 'Awaiting Approval',
  TOP_5_OVERDUE: 'Top 5 Overdue',
  NO_OVERDUE_ORDERS: 'No overdue orders',
  TOP_5_RECENT: 'Top 5 Recent',
  NO_RECENT_ORDERS: 'No recent orders',
  FAILED_TO_FETCH_OVERDUE: 'Failed to fetch overdue orders',
  FAILED_TO_FETCH_RECENT: 'Failed to fetch recent orders',
} as const

// Kanban Board Labels
export const KANBAN_LABELS = {
  TITLE: 'Kanban Board',
  SUBTITLE: 'Drag and drop to update repair order status',
  CANNOT_MOVE: 'Cannot move order',
  CANNOT_MOVE_HERE: 'Cannot move order here',
  TRANSITION_NOT_ALLOWED: 'This transition is not allowed',

  // Status Display Names
  STATUS: {
    NEW: 'New',
    IN_PROGRESS: 'In Progress',
    COMPLETED: 'Completed',
    WAITING_PARTS: 'Waiting Parts',
    AWAITING_APPROVAL: 'Awaiting Approval',
  },
} as const

// Filter Labels
export const FILTER_LABELS = {
  TITLE: 'Filters',
  SEARCH_PLACEHOLDER: 'Search by RO ID, customer, vehicle...',

  PRIORITY: 'Priority',
  ALL_PRIORITY: 'All Priority',
  HIGH_PRIORITY: 'High Priority',
  NORMAL_PRIORITY: 'Normal Priority',

  ASSIGNED_TECH: 'Assigned Tech',
  ALL_TECHNICIANS: 'All Technicians',
} as const

// Navigation Labels
export const NAV_LABELS = {
  REPORTS: 'Reports',
  SETTINGS: 'Settings',
} as const

// Welcome/Auth Labels
export const WELCOME_LABELS = {
  TITLE: 'TekBoard',
  SUBTITLE: 'Kanban workflow for auto repair shops',
  FEATURE_TRACKING: '✓ Real-time repair order tracking',
  FEATURE_ASSIGNMENT: '✓ Technician assignment & workload management',
  FEATURE_VALIDATION: '✓ Status transitions with validation',
  DEMO_CTA: 'Continue as Demo User',
  DEMO_SUBTITLE: 'No authentication required for demo',
  DEMO_USER_NAME: 'Demo User',
  DEMO_USER_EMAIL: 'demo@tekboard.com',
} as const
