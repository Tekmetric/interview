export enum RepairServiceStatus {
  PENDING = 'PENDING',
  DIAGNOSED = 'DIAGNOSED',
  APPROVED = 'APPROVED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
}

export function getStatusDisplayText(status: RepairServiceStatus): string {
  return status.replace('_', ' ');
}

export function getAllStatuses(): RepairServiceStatus[] {
  return Object.values(RepairServiceStatus);
}
