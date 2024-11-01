import { formatDistance } from 'date-fns/formatDistance'

export const formatCreatedDate = (date: string): string =>
  formatDistance(date, new Date(), { addSuffix: true })
